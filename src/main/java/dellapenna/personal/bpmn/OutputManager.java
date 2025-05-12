package dellapenna.personal.bpmn;

import java.io.PrintWriter;

/**
 *
 * @author giuse
 */
public class OutputManager {

    private static OutputManager instance = null;

    public static OutputManager getInstance() {
        if (instance == null) {
            instance = new OutputManager();
        }
        return instance;
    }

    public enum MessageType {
        MANDATORY(0), INFO(5), WARNING(10), ERROR(15), DEBUG(20);
        private final int level;

        private MessageType(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    };
    private final String nesting_string;
    private final String message_prefix;
    private MessageType max_output_level;
    private int max_message_length = 1000;

    private final PrintWriter output;

    private static final String DEF_NESTING_STRING = "  ";
    private static final String DEF_MESSAGE_PREFIX = "* ";
    private static final PrintWriter DEF_output = new PrintWriter(System.out, true);

    public OutputManager() {
        this(DEF_output, DEF_NESTING_STRING, DEF_MESSAGE_PREFIX);
    }

    public OutputManager(PrintWriter output, String nesting_string, String message_prefix) {
        this.output = output;
        this.nesting_string = nesting_string;
        this.message_prefix = message_prefix;
        this.max_output_level = MessageType.DEBUG;
    }

    public void setMaxOutputLevel(MessageType max_output_level) {
        this.max_output_level = max_output_level;
    }

    public void emit(MessageType type, int nesting, String channel, String message) {
        if (type.getLevel() <= max_output_level.getLevel()) {
            String msg = "";
            for (int n = 0; n < nesting; ++n) {
                msg += nesting_string;
            }
            msg += message_prefix;
            if (channel != null && !channel.isBlank()) {
                msg += "[" + channel + "] ";
            }
            if (type != MessageType.MANDATORY) {
                msg += "<" + type.toString() + "> ";
            }
            msg += ellipsize(message, max_message_length);
            output.println(msg);
        }
    }

    public void emit(String message) {
        emit(MessageType.MANDATORY, 0, null, message);
    }

    public void emit(MessageType type, String message) {
        emit(type, 0, null, message);
    }

    public void emit(String channel, String message) {
        emit(MessageType.MANDATORY, 0, channel, message);
    }

    public void emit(int nesting, String channel, String message) {
        emit(MessageType.MANDATORY, nesting, channel, message);
    }

    public void emit(MessageType type, int nesting, String message) {
        emit(type, nesting, null, message);
    }

    public void emit(int nesting, String message) {
        emit(MessageType.MANDATORY, nesting, null, message);
    }

    public void setMaxMessageLength(int max_message_length) {
        this.max_message_length = max_message_length;
    }

    ////
    public String ellipsize(String text, int max) {

        if (text.length() <= max) {
            return text;
        }

        int end = text.lastIndexOf(' ', max - 3);

        if (end == -1) {
            return text.substring(0, max - 3) + "...";
        }

        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);
            if (newEnd == -1) {
                newEnd = text.length();
            }

        } while ((text.substring(0, newEnd) + "...").length() < max);

        return text.substring(0, end) + "...";
    }

}
