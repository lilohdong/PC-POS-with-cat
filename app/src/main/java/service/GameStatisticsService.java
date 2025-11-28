package service;

import dao.GameDAO;
import dto.GameStatisticDTO;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GameStatisticsService {
    private static GameStatisticsService gameStatisticsService;

    private GameStatisticsService() {
    }

    public static GameStatisticsService getInstance() {
        if (gameStatisticsService == null) {
            gameStatisticsService = new GameStatisticsService();
        }
        return gameStatisticsService;
    }

    public void initStatisticsData(DefaultTableModel tm) {
        tm.setRowCount(0);
        GameDAO dao = GameDAO.getInstance();
        List<GameStatisticDTO> list = dao.getStatistics();

        if (list == null || list.isEmpty()) {
            return;
        }
        for (GameStatisticDTO dto : list) {
            Object[] rowData = {
                    dto.getRank(),
                    dto.getGameName(),
                    dto.getTotalTime(),
                    dto.getUsers()
            };
            tm.addRow(rowData);
        }
    }
}