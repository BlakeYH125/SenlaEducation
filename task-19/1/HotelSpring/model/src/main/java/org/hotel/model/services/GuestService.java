package org.hotel.model.services;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hotel.constants.CommandConstants;
import org.hotel.constants.ParametersConstants;
import org.hotel.constants.TimeConstants;
import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.hotel.model.exceptions.GuestNotFoundException;
import org.hotel.model.exceptions.RoomNotFoundException;
import org.hotel.model.exceptions.RoomNotOccupiedException;
import org.hotel.model.repository.GuestRepository;
import org.hotel.model.enums.SortType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class GuestService {
    /**
     * Логгер.
     */
    private static final Logger LOGGER = LogManager.getLogger(GuestService.class);

    /**
     * Репозиторий для работы с гостями в БД.
     */
    private final GuestRepository guestRepository;

    /**
     * Управление комнатами.
     */
    private final RoomService roomService;

    public GuestService(final GuestRepository guestRepositoryP, final RoomService roomServiceP) {
        this.guestRepository = guestRepositoryP;
        this.roomService = roomServiceP;
    }

    public int getGuestsCount() {
        return guestRepository.findCurrentGuestsInHotel().size();
    }

    public void addGuest(final Guest guest) {
        guestRepository.save(guest);
    }

    public Guest getGuest(final String id) {
        return guestRepository.getGuest(id);
    }

    public void setEvicted(final String id) {
        guestRepository.setEvicted(getGuest(id));
    }

    public List<Guest> getActualGuests() {
        return guestRepository.findCurrentGuestsInHotel();
    }

    public boolean isThereGuest(final String id) {
        return guestRepository.getGuest(id) != null;
    }

    public List<Guest> getActualGuestsWithSort(final SortType sortType) {
        List<Guest> listGuests = guestRepository.findCurrentGuestsInHotel();
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }


    public String importGuests(MultipartFile multipartFile) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            String line;
            int successCount = 0;
            int errorCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == ParametersConstants.GUEST_PARAMETERS_COUNT) {
                    try {
                        addGuest(new Guest(parts[CommandConstants.COMMAND_ZERO], parts[CommandConstants.COMMAND_ONE], Integer.parseInt(parts[CommandConstants.COMMAND_TWO])));
                        successCount++;
                    } catch (Exception e) {
                        LOGGER.error("Ошибка обработки строки " + line + ": " + e.getMessage());
                        errorCount++;
                    }
                } else {
                    LOGGER.error("Ошибка при импорте. Неверное количество параметров");
                }
            }
            return "Импорт успешен. Количество ошибок: " + errorCount + ", количество успешно считанных строк: " + successCount;
        }
    }

    public BigDecimal getTotalCost(String roomId) {
        if (!roomService.isThereRoom(roomId)) {
            throw new RoomNotFoundException();
        }
        if (!roomService.isOccupied(roomId)) {
            throw new RoomNotOccupiedException();
        }
        Room room = roomService.getRoom(roomId);
        List<Guest> guests = guestRepository.findCurrentGuestsInRoom(room);
        if (guests.isEmpty()) {
            throw new GuestNotFoundException();
        }
        long millis = guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime();
        BigDecimal days = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(TimeConstants.MSEC_IN_DAY), 2, RoundingMode.HALF_UP);
        return room.getPrice().multiply(days);
    }

    public String exportGuests() {
        List<Guest> guests = getActualGuests();
        StringBuilder stringBuilder = new StringBuilder();
        for (Guest guest : guests) {
            String[] data = new String[ParametersConstants.GUEST_PARAMETERS_COUNT];
            data[0] = guest.getId();
            data[1] = guest.getFullName();
            data[2] = String.valueOf(guest.getAge());
            String result = String.join(";", data);
            stringBuilder.append(result).append("\n");
        }
        return stringBuilder.toString();
    }
}
