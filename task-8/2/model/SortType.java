package model;

import java.io.Serializable;

public enum SortType implements Serializable {
    PRICE(), CAPACITY(), STARS(), ALPHABET(), DATE(), SECTION();
}
