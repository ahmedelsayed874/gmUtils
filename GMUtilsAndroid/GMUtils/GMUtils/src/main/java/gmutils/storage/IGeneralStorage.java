package gmutils.storage;

import java.util.List;
import java.util.Set;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public interface IGeneralStorage extends IStorage {

    void saveToList(String listName, String... value);

    void saveToList(String listName, List<String> value);


    void saveToList(String listName, boolean onTop, String... value);

    void saveToList(String listName, boolean onTop, List<String> value);


    List<String> retrieveList(String listName);

    void removeFromList(String listName, String value);

    void clearList(String listName);


    //---------------------------------------------------------------------


    void saveToSet(String setName, String... value);

    void saveToSet(String setName, List<String> value);


    Set<String> retrieveSet(String setName);

    void removeFromSet(String setName, String value);

    void clearSet(String listName);

}
