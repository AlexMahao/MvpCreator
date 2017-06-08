package com.spearbothy;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mahao on 17-6-7.
 */
public class MvpCreatorAction extends AnAction {

    private Project project;
    private JDialog jFrame;
    JTextField name;
    JTextField username;
    JRadioButton activityJB;
    JRadioButton fragmentJB;
    /*包名*/
    private String packagebase = "";

    private String codePath;


    @Override
    public void actionPerformed(AnActionEvent e) {
        //
        project = e.getData(PlatformDataKeys.PROJECT);

        // 读取包名
        packagebase = readPackageName();

        codePath = project.getBasePath() + "/app/src/main/java/" + packagebase.replace(".", "/") + "/";

        // 显示对话框
        showDialog();

    }


    private void createFile() {
        createViewFile();
        createPresenterFile();
        if (activityJB.isSelected()) {
            createActivityFile();
        }
        if (fragmentJB.isSelected()) {
            createFragmentFile();
        }
    }

    private void createFragmentFile() {
        String token = name.getText();

        String content = readFile("templateFragment.txt");
        // 替换author
        content = content.replace("$author", username.getText());
        // 替换时间
        content = content.replace("$time", getNowDateShort());
        // 替换类名
        content = content.replace("$name", token);
        // 替换包名
        content = content.replace("$packagename", packagebase);

        writetoFile(content, codePath + "ui", token + "Fragment.java");
    }

    private void createActivityFile() {
        String token = name.getText();

        String content = readFile("templateActivity.txt");
        // 替换author
        content = content.replace("$author", username.getText());
        // 替换时间
        content = content.replace("$time", getNowDateShort());
        // 替换类名
        content = content.replace("$name", token);
        // 替换包名
        content = content.replace("$packagename", packagebase);

        writetoFile(content, codePath + "ui", token + "Activity.java");
    }

    private void createPresenterFile() {
        String token = name.getText();

        String content = readFile("templatePresenter.txt");
        // 替换author
        content = content.replace("$author", username.getText());
        // 替换时间
        content = content.replace("$time", getNowDateShort());
        // 替换类名
        content = content.replace("$name", token);
        // 替换包名
        content = content.replace("$packagename", packagebase);

        writetoFile(content, codePath + "presenter", token + "Presenter.java");
    }

    private void createViewFile() {
        String token = name.getText();

        String content = readFile("templateView.txt");
        // 替换author
        content = content.replace("$author", username.getText());
        // 替换时间
        content = content.replace("$time", getNowDateShort());
        // 替换类名
        content = content.replace("$name", token);
        // 替换包名
        content = content.replace("$packagename", packagebase);

        writetoFile(content, codePath + "view", token + "View.java");
    }

    public String getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String filename) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/template/" + filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
            Messages.showMessageDialog(e.getMessage(), "提示", Messages.getInformationIcon());
            e.printStackTrace();
        }
        return content;
    }

    public byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
            Messages.showMessageDialog(e.getMessage(), "提示", Messages.getInformationIcon());
            e.printStackTrace();
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }


    private String readPackageName() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/app/src/main/AndroidManifest.xml");

            NodeList dogList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < dogList.getLength(); i++) {
                Node dog = dogList.item(i);
                Element elem = (Element) dog;
                return elem.getAttribute("package");
            }
        } catch (Exception e) {
            Messages.showMessageDialog(e.getMessage(), "提示", Messages.getInformationIcon());
            e.printStackTrace();
        }
        return "";
    }

    private void showDialog() {
        jFrame = new JDialog();// 定义一个窗体Container container = getContentPane();
        jFrame.setModal(true);
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel panelname = new JPanel();// /定义一个面板
        panelname.setLayout(new GridLayout(1, 2));
        panelname.setBorder(BorderFactory.createTitledBorder("命名"));

        name = new JTextField();
        name.setText("请输入组件名字");
        panelname.add(name);

        username = new JTextField();
        username.setText("请输入注释的作者");
        panelname.add(username);

        container.add(panelname);


        activityJB = new JRadioButton("Activity");// 定义一个单选按钮
        fragmentJB = new JRadioButton("Fragment");// 定义一个单选按钮

        activityJB.setSelected(true);

        JPanel panel = new JPanel();// /定义一个面板

        panel.setBorder(BorderFactory.createTitledBorder("选择生成代码的类型"));// 定义一个面板的边框显示条
        panel.setLayout(new GridLayout(1, 2));// 定义排版，一行三列
        panel.add(activityJB);// 加入组件
        panel.add(fragmentJB);// 加入组件

        ButtonGroup group = new ButtonGroup();
        group.add(activityJB);
        group.add(fragmentJB);
        container.add(panel);// 加入面板

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        Button cancle = new Button();
        cancle.setLabel("取消");
        cancle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        Button ok = new Button();
        ok.setLabel("确定");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        menu.add(cancle);
        menu.add(ok);
        container.add(menu);


        jFrame.setSize(400, 200);
        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);
    }


    private void onOK() {
        jFrame.dispose();
        createFile();
        Messages.showMessageDialog("创建成功", "提示", Messages.getInformationIcon());
        project.getProjectFile().refresh(false, true);
    }

    private void onCancel() {
        jFrame.dispose();
    }
}
