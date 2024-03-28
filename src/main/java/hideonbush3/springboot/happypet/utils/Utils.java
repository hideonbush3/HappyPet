package hideonbush3.springboot.happypet.utils;

import java.util.UUID;

public class Utils {
    public static String createUuid(int beginIndex, int endIndex){
        UUID randomUuid = UUID.randomUUID();
        String uuidString = randomUuid.toString().replace("-", "").substring(beginIndex, endIndex);
        return uuidString;
    }
}
