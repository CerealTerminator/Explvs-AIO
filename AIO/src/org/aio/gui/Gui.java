package org.aio.gui;

import org.aio.gui.conf_man.ConfigManager;
import org.aio.gui.task_panels.TaskPanel;
import org.aio.gui.task_panels.TaskPanelFactory;
import org.aio.tasks.Task;
import org.aio.tasks.TaskType;
import org.aio.tasks.TutorialIslandTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Gui {

    public static final Color DARK_GREY = Color.decode("#181818");

    private JDialog gui;

    private boolean started;
    private JPanel taskList = new JPanel();

    private ArrayList<TaskPanelContent> taskPanels = new ArrayList<>();

    public Gui() {
        gui = new JDialog();
        gui.setTitle("Explv's AIO");
        gui.setModal(true);
        gui.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        gui.setBackground(DARK_GREY);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(DARK_GREY);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), null));

        final JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setText("<html><span color='#33b5e5'>Explv</span>'s AIO</html>");
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        final JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        controlsPanel.setBackground(DARK_GREY);
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        final JPanel saveLoadPanel = new JPanel();
        saveLoadPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        saveLoadPanel.setBackground(DARK_GREY);
        controlsPanel.add(saveLoadPanel);
        saveLoadPanel.setBorder(BorderFactory.createTitledBorder(null, "Save / Load", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));

        saveLoadPanel.add(createButtonPanel(
                "Save",
                "Save",
                "saveIcon.png",
                "saveIconHover.png",
                e -> {
                    if (!validate(gui)) {
                        JOptionPane.showMessageDialog(gui, "Fields highlighted in red are invalid", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        saveConfig();
                    }
                }
        ));

        saveLoadPanel.add(createButtonPanel(
                "Load",
                "Load",
                "loadIcon.png",
                "loadIconHover.png",
                e -> loadConfig()
        ));

        controlsPanel.add(createSpacerPanel());

        final JPanel addTaskPanel = new JPanel();
        addTaskPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        addTaskPanel.setBackground(DARK_GREY);
        controlsPanel.add(addTaskPanel);
        addTaskPanel.setBorder(BorderFactory.createTitledBorder(null, "Add a Task", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));

        addTaskPanel.add(createButtonPanel(
                "Level",
                "Level Task",
                "levelIcon.png",
                "levelIconHover.png",
                e -> addTask(TaskType.LEVEL)
        ));

        addTaskPanel.add(createButtonPanel(
                "Resource",
                "Resource Task",
                "resourceIcon.png",
                "resourceIconHover.png",
                e -> addTask(TaskType.RESOURCE)
        ));

        addTaskPanel.add(createButtonPanel(
                "Timed",
                "Timed Task",
                "timedIcon.png",
                "timedIconHover.png",
                e -> addTask(TaskType.TIMED)
        ));

        addTaskPanel.add(createButtonPanel(
                "Loop",
                "Loop Previous Tasks",
                "loopIcon.png",
                "loopIconHover.png",
                e -> addTask(TaskType.LOOP)
        ));

        addTaskPanel.add(createButtonPanel(
                "Quest",
                "Quest Task",
                "questIcon.png",
                "questIconHover.png",
                e -> addTask(TaskType.QUEST)
        ));

        addTaskPanel.add(createButtonPanel(
                "Grand Exchange",
                "GE Task",
                "geIcon.png",
                "geIconHover.png",
                e -> addTask(TaskType.GRAND_EXCHANGE)
        ));

        addTaskPanel.add(createButtonPanel(
                "Break",
                "Break Task",
                "breakIcon.png",
                "breakIconHover.png",
                e -> addTask(TaskType.BREAK)
        ));
        controlsPanel.add(createSpacerPanel());

        final JPanel startPanel = new JPanel();
        startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        startPanel.setBackground(DARK_GREY);
        controlsPanel.add(startPanel);
        startPanel.setBorder(BorderFactory.createTitledBorder(null, "Start", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));

        startPanel.add(createButtonPanel(
                "Start",
                "Start",
                "startIcon.png",
                "startIconHover.png",
                e -> {
                    if (!validate(gui)) {
                        JOptionPane.showMessageDialog(gui, "Fields highlighted in red are invalid", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        started = true;
                        close();
                    }
                }
        ));

        controlsPanel.add(startPanel);

        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        taskList.setBackground(DARK_GREY);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(DARK_GREY);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), null));
        scrollPane.setViewportView(taskList);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        gui.setMinimumSize(new Dimension(700, 300));
        gui.setMaximumSize(new Dimension(2000, 2000));

        gui.setLocationRelativeTo(null);
        gui.setContentPane(mainPanel);
        gui.pack();
        gui.setResizable(true);
    }

    private boolean validate(final Container container) {
        boolean valid = true;

        Component[] comps = container.getComponents();
        for (Component component : comps) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                if (jComponent.getInputVerifier() != null) {
                    if (!jComponent.getInputVerifier().verify(jComponent)) {
                        valid = false;
                    }
                }
            }
            if (component instanceof Container) {
                if (!validate((Container) component)) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    private JPanel createSpacerPanel() {
        final JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(DARK_GREY);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20), null));
        return panel;
    }

    private JPanel createButtonPanel(final String label, final String toolTip, final String icon, final String rolloverIcon, ActionListener callback) {
        JPanel buttonPanel = new JPanel(new BorderLayout(0, 3));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        buttonPanel.setBackground(DARK_GREY);

        final JLabel panelLabel = new JLabel();
        panelLabel.setForeground(Color.WHITE);
        panelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelLabel.setText(label);
        buttonPanel.add(panelLabel, BorderLayout.SOUTH);

        JButton button = IconButton.createButton(toolTip, icon, rolloverIcon, callback);

        buttonPanel.add(button, BorderLayout.NORTH);

        return buttonPanel;
    }

    /**
     * Public getter for the entire ordered task list
     * <p>
     * Note: Intentionally rebuilds the tasks, so each call returns a fresh list of task instances
     */
    public final ArrayList<Task> getTasksAsList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TutorialIslandTask());

        int taskIndex = 1;
        for (TaskPanelContent taskPanel : taskPanels) {
            Task task = taskPanel.panel.toTask();
            task.setExecutionOrder(taskIndex);
            taskIndex++;

            tasks.add(task);
        }

        return tasks;
    }

    public boolean isStarted() {
        return started;
    }

    public void open() {
        gui.setVisible(true);
    }

    public boolean isOpen() {
        return gui.isVisible();
    }

    public void close() {
        gui.setVisible(false);
        gui.dispose();
    }

    private TaskPanel addTask(final TaskType taskType) {
        TaskPanel taskPanel = TaskPanelFactory.createTaskPanel(taskType);

        if (taskPanel == null) {
            throw new IllegalArgumentException(String.format("Task type %s not supported.", taskType.toString()));
        }
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menuItemDelete = new JMenuItem("Delete");
        JMenuItem menuItemUp = new JMenuItem("Move up");
        JMenuItem menuItemDown = new JMenuItem("Move down");

        contextMenu.add(menuItemDelete);
        contextMenu.add(new JSeparator());
        contextMenu.add(menuItemUp);
        contextMenu.add(menuItemDown);

        ArrayList<Component> components = new ArrayList<>();
        components.add(taskPanel.getPanel());
        components.add(Box.createRigidArea(new Dimension(5, 10)));
        TaskPanelContent taskPanelContent = new TaskPanelContent(taskPanel, components);
        taskPanels.add(taskPanelContent);

        for (Component component : components) {
            taskList.add(component);
        }

        taskPanel.getPanel().setMaximumSize(new Dimension(taskPanel.getPanel().getMaximumSize().width, taskPanel.getPanel().getPreferredSize().height));

        /*
         UI Actions
        */
        menuItemDelete.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                taskPanels.remove(taskPanelContent);

                for (Component component : taskPanelContent.components) {
                    taskList.remove(component);
                }

                taskList.revalidate();
                taskList.repaint();
                gui.pack();
            });
        });

        menuItemUp.addActionListener(e -> {
            int from = taskPanels.indexOf(taskPanelContent);
            swapTasks(from, from - 1);
        });

        menuItemDown.addActionListener(e -> {
            int from = taskPanels.indexOf(taskPanelContent);
            swapTasks(from, from + 1);
        });

        taskPanel.getPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    contextMenu.show(taskPanel.getPanel(), e.getX(), e.getY());
                }
            }
        });

        gui.pack();

        return taskPanel;
    }

    private void swapTasks(int from, int to) {
        SwingUtilities.invokeLater(() -> {
            if (from < 0 || from >= taskPanels.size() || to < 0 || to >= taskPanels.size()) {
                return;
            }

            Collections.swap(taskPanels, from, to);

            taskList.removeAll();
            for (TaskPanelContent redrawTaskPanelContent : taskPanels) {
                for (Component component : redrawTaskPanelContent.components) {
                    taskList.add(component);
                }
            }

            taskList.revalidate();
            taskList.repaint();
            gui.pack();
        });
    }

    private void saveConfig() {
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {

                JSONObject jsonObject = new JSONObject();

                JSONArray taskJSONArray = new JSONArray();

                for (TaskPanelContent taskPanel : taskPanels) {
                    taskJSONArray.add(taskPanel.panel.toJSON());
                }

                jsonObject.put("tasks", taskJSONArray);

                ConfigManager configManager = new ConfigManager();
                configManager.saveConfig(jsonObject);
                return null;
            }
        }.execute();
    }

    private void loadConfig() {
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                ConfigManager configManager = new ConfigManager();

                Optional<JSONObject> jsonObjectOpt = configManager.readConfig();

                if (!jsonObjectOpt.isPresent()) {
                    return null;
                }

                taskList.removeAll();
                taskPanels.clear();

                JSONObject jsonObject = jsonObjectOpt.get();

                JSONArray tasks;

                if (jsonObject.containsKey("org/aio/tasks")) {
                    tasks = (JSONArray) jsonObject.get("org/aio/tasks");
                } else {
                    tasks = (JSONArray) jsonObject.get("tasks");
                }

                for (Object task : tasks) {
                    JSONObject taskJSON = (JSONObject) task;
                    addTask(TaskType.valueOf((String) taskJSON.get("type"))).fromJSON(taskJSON);
                }

                taskList.validate();
                taskList.repaint();
                gui.validate();
                gui.repaint();
                return null;
            }
        }.execute();
    }

    /**
     * Task panel content for use with tracking rendered/interactive content for each task panel instance
     */
    class TaskPanelContent {
        TaskPanel panel;
        List<Component> components;

        TaskPanelContent(TaskPanel panel, List<Component> components) {
            this.panel = panel;
            this.components = components;
        }

    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.open();
    }
}
