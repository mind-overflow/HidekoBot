package wtf.beatrice.hidekobot.objects.comparators;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * This class gets two linked lists, and compares their first value alphabetically.
 */
public class MessageCommandAliasesComparator implements Comparator<LinkedList<String>> {

    @Override
    public int compare(LinkedList<String> linkedList, LinkedList<String> t1) {

        if(linkedList.isEmpty()) return 0;
        if(t1.isEmpty()) return 0;

        return linkedList.get(0).compareTo(t1.get(0));
    }
}
