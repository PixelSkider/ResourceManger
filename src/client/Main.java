package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.channels.FileChannel;

public class Main extends JFrame {
    public static int width, height;
    public static String savepath_text,readpath_text,savename_text,readname_text;
    JButton copy, chose,click,save;
    JTextField path,savepath;
    JTextArea date;
    JScrollPane datePanel;

    public Main() throws IOException {
        createjson();
        width = 700;
        height = 500;
        createjson();

        setTitle(" Welcome to use Resource Manger :) ");
        setLayout(null);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(Main.EXIT_ON_CLOSE);
        setResizable(false);

        readpath_text = readConfig("readPath");
        savepath_text = readConfig("savePath");
        Swing();
        Event();
    }

    public void Swing() {


        copy = new JButton();
        copy.setLocation(10, 10);
        copy.setSize(100, 50);
        copy.setText("Copy");

        chose = new JButton();
        chose.setLocation(10, 70);
        chose.setSize(100, 50);
        chose.setText("Chose");

        click = new JButton();
        click.setLocation(330, 10);
        click.setSize(100, 50);
        click.setText("Click");

        save = new JButton();
        save.setLocation(330, 70);
        save.setSize(100, 50);
        save.setText("Save");

        savepath = new JTextField();
        savepath.setLocation(120, 10);
        savepath.setSize(200, 50);
        savepath.setText(savepath_text);

        path = new JTextField();
        path.setLocation(120, 70);
        path.setSize(200, 50);
        path.setText(readpath_text);

        date = new JTextArea(999, 1);
        date.setLocation(10, 140);
        date.setSize(310, 100);
        date.setText("date:");
        date.setLineWrap(true);

        datePanel = new JScrollPane(date);
        datePanel.setLocation(10, 140);
        datePanel.setSize(310, 100);


        this.add(copy);
        this.add(chose);
        this.add(click);
        this.add(save);
        this.add(path);
        this.add(savepath);
        this.add(datePanel);

    }

    public void Event() {
        chose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = 0;
                FileSystemView fsv = FileSystemView.getFileSystemView();
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(fsv.getHomeDirectory());
                fc.setAcceptAllFileFilterUsed(false);
                fc.setMultiSelectionEnabled(false);
                fc.setDialogTitle("请选择材质包");
                fc.setApproveButtonText("确定");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                result = fc.showOpenDialog(Main.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    readpath_text = fc.getSelectedFile().getPath();
                    readname_text = fc.getSelectedFile().getName();
                    path.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = 0;
                JFileChooser fc = new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                fc.setCurrentDirectory(fsv.getHomeDirectory());
                fc.setDialogTitle("请选择要保存地址");
                fc.setApproveButtonText("确定");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                result = fc.showOpenDialog(Main.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    savepath_text = fc.getSelectedFile().getPath();
                    savename_text = fc.getSelectedFile().getName();
                    savepath.setText(fc.getSelectedFile().getPath());
                }

            }
        });

        click.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Files(readpath_text,savepath_text,savename_text,readname_text);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveConfig();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    public void Files(String readpath,String savepath_text,String savename_text,String readname_text) throws IOException {
        File read = new File(readpath);
        File save = new File(savepath_text,readname_text);
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(read).getChannel();
            outputChannel = new FileOutputStream(save).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {

        }
    }

    public void createjson() throws IOException {
        File tmp = new File(getRunPath() + "\\ResourceManger\\test.tmp");
        if (!tmp.exists()){
            JsonObject jsonObject = new JsonObject();
            Path path = Paths.get(getRunPath() + "\\ResourceManger\\config.json");
            JsonObject modJsonObject = new JsonObject();
            modJsonObject.addProperty("readPath","Path...");
            modJsonObject.addProperty("savePath","Path...");
            jsonObject.add("Path", modJsonObject);
            Files.write(path,new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void saveConfig() throws IOException {
        JsonObject jsonObject = new JsonObject();
        Path path = Paths.get(getRunPath() + "\\ResourceManger\\config.json");
        JsonObject modJsonObject = new JsonObject();
        modJsonObject.addProperty("readPath",readpath_text);
        modJsonObject.addProperty("savePath",savepath_text);
        jsonObject.add("Client", modJsonObject);
        try {
            Files.write(path,new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readConfig(String text) throws IOException {

        createFile();
        String haw = null;
        try {
            Path path = Paths.get( getRunPath() + "\\ResourceManger\\config.json");
            JsonObject jsonObject = new Gson().fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), JsonObject.class);
            if (jsonObject.has("Path")) {
                JsonObject modJsonObject = jsonObject.get("Path").getAsJsonObject();
                if (modJsonObject.has(text)) {
                    haw = modJsonObject.get(text).getAsString();
                    return haw;
                }
            }else {
                createjson();
                return null;
            }
        } catch (IOException e) {
            createjson();
            e.printStackTrace();
            return null;
        }
        return haw;
    }

    public void createFile() throws IOException {
        File tmp = new File(getRunPath() + "\\ResourceManger\\test.tmp");
        File json = new File(getRunPath() + "\\ResourceManger\\config.json");
        File ResourceManger = new File(getRunPath() + "\\ResourceManger\\");
        if (!ResourceManger.exists()){
            ResourceManger.mkdir();
        }
        if (!tmp.exists()){
            tmp.createNewFile();
        }
        if (!json.exists()){
            json.createNewFile();
        }

    }

    public String getRunPath() {
        return System.getProperty("user.dir");
    }

}
