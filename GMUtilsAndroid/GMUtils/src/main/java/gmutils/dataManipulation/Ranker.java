package gmutils.dataManipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Ranker {

    public Integer[] rank(List<Integer> scores) {
        if (scores == null || scores.size() == 0) return null;

        Integer[] scoresRank = new Integer[scores.size()];
        int rank = 1;

        Map<Integer, Integer> scoresMap = new HashMap<>();
        for (int i = 0; i < scores.size(); i++) {
            scoresMap.put(i, scores.get(i));
        }

        while (true) {
            int max = -1;
            int position = -1;
            List<Integer> similarPositions = null;

            Set<Map.Entry<Integer, Integer>> entries = scoresMap.entrySet();
            for (Map.Entry<Integer, Integer> entry: entries) {
                int score = entry.getValue();
                if (score > max) {
                    max = score;
                    position = entry.getKey();
                    similarPositions = new ArrayList<>();

                } else if (score == max){
                    similarPositions.add(entry.getKey());
                }
            }

            if (position < 0) break;

            scoresRank[position] = rank;
            scoresMap.remove(position);

            for (int similarPosition : similarPositions) {
                scoresRank[similarPosition] = rank;
                scoresMap.remove(similarPosition);
            }

            rank++;
        }

        return scoresRank;
    }
}
