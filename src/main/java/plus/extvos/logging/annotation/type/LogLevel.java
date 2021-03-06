package plus.extvos.logging.annotation.type;

/**
 * @author Mingcai SHEN
 */

public enum LogLevel {

    /**
     *
     */
    NORMAL("Normal"),
    IMPORTANT("Important"),
    CRITICAL("Critical");

    private String value;

    LogLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
