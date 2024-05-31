/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meu.sebra;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.awt.Cursor;
import java.awt.Desktop;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.xml.sax.helpers.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.ibm.icu.text.Transliterator;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import javax.swing.Box;
import meu.config.Config;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.Math;

/**
 *
 * @author thristov
 */
public class Sebra extends javax.swing.JFrame {

    JFrame f;
    public String inCsv;
    public String orgCsv;
    public String saltTxt;
    public String outCsv;
    public String folder;
    public Path pathInCsv;
    public Path pathOrgCsv;
    public Path pathSaltTxt;
    public Path pathOutCsv;
    public Path pathFolder;
    public String inDelRegNumCsv;  // Файл за изтриване по номер на регистрация! | File to delete by registration number!
    public String outDelRegNumCsv;  // Файл с резултатни данни от изтриването по номер на регистрация! | Delete result data file by registration number!
    public String rejectedDelRegNumCsv;  // Файл с отхвърлени (не изтрити автоматично) записи при изтриването по номер на регистрация! | File with rejected (not automatically deleted) records when deleting by registration number!
    public String autodelRegNumCsv;  // Файл с автоматично изтрити записи при изтриването по номер на регистрация! | File with automatically deleted records when deleting by registration number!
    public String regNumCsv;  // Файл-масив с номера на регистрация и себра кодове! | File-array with registration numbers and sebra codes!
    public String inDelSebraCodesCsv;  // Файл за изтриване по себра код! | File to delete by sebra code!
    public String outDelSebraCodesCsv;  // Файл с резултатни данни от изтриването по себра код! | A file with the result data of the deletion by sebra code!
    public String sebraCodesCsv;  // Файл-масив със себра кодове! | File-array with sebra codes!
    public String autodelDelSebraCodesCsv;  // Файл с изтрити записи при изтриване по себра код! | Deleted records file when deleting by sebra code!
    public Path pathInDelRegNumCsv;
    public Path pathOutDelRegNumCsv;
    public Path pathRegNumCsv;
    public Path pathRejectedDelRegNumCsv;
    public Path pathAutodelRegNumCsv;
    public Path pathInDelSebraCodesCsv;
    public Path pathOutDelSebraCodesCsv;
    public Path pathSebraCodesCsv;
    public Path pathAutodelSebraCodesCsv;
    public String salt;
    public Config config;
    public String taText = "";      // TextArea
    public String slText = "";      // StatusLabel
    public static Transliterator TRANSLITERATOR = Transliterator.getInstance("Latin-Cyrillic");
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");  // ("d/M/yyyy")
    public static MessageDigest DIGEST;
    public static Pattern WHITESPACE = Pattern.compile("\\s+");

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates new form Sebra
     */
    public Sebra() {
        initComponents();
        f = new JFrame();
        config = new Config();
        inCsv = "";
        orgCsv = "";
        saltTxt = "";
        outCsv = "";
        folder = "";
        inDelRegNumCsv = "";
        outDelRegNumCsv = "";
        rejectedDelRegNumCsv = "";
        autodelRegNumCsv = "";
        regNumCsv = "";
        inDelSebraCodesCsv = "";
        outDelSebraCodesCsv = "";
        sebraCodesCsv = "";
        autodelDelSebraCodesCsv = "";

        // KeyPairGenerator generator = null;
        // KeyPair pair;
        // PrivateKey privateKey;
        // PublicKey publicKey;
        // 
        // try {
        //     generator = KeyPairGenerator.getInstance("RSA");
        //     generator.initialize(2048);
        //     pair = generator.generateKeyPair();
        //     privateKey = pair.getPrivate();
        //     publicKey = pair.getPublic();
        //     try (FileOutputStream fos_public = new FileOutputStream("key/sebra_public.key")) {
        //         fos_public.write(publicKey.getEncoded());
        //     } catch (FileNotFoundException ex) {
        //         Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        //     } catch (IOException ex) {
        //         Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        //     }
        //     try (FileOutputStream fos_private = new FileOutputStream("key/sebra_private.key")) {
        //         fos_private.write(privateKey.getEncoded());
        //     } catch (FileNotFoundException ex) {
        //         Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        //     } catch (IOException ex) {
        //         Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        //     }
        // } catch (NoSuchAlgorithmException ex) {
        //     Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        // }
        removeDataGeneralStatisticsTextArea();
        taText = " 1. Изберете: Входящ файл!";
        setDataGeneralStatisticsTextArea(taText);
        taText = " 2. Изберете: Файл с АО!";
        setDataGeneralStatisticsTextArea(taText);
        taText = " 3. Стартирайте: Анонимизиране на данните!";
        setDataGeneralStatisticsTextArea(taText);
        taText = "------------------------------------------------------------------------------------------------------------------";
        setDataGeneralStatisticsTextArea(taText);
        taText = " • Анонимизират се четири полета от получения файл от БОРИКА:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - CLIENT_RECEIVER_NAME (Наименование на получателя)";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - CLIENT_RECEIVER_ACC (IBAN на бенефициента)";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - REASON1 (Основание за плащане I)";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - REASON2 (Основание за плащане II)";
        setDataGeneralStatisticsTextArea(taText);
        taText = "";
        setDataGeneralStatisticsTextArea(taText);
        taText = " • CLIENT_RECEIVER_NAME:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Проверява се за име на ФЛ - заменя се с: 'Физическо лице', само имената.";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Проверява се за ЕГН:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ Абревиатурата ЕГН се изтрива;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ 10-те цифри се проверяват за валидно ЕГН и ако то е такова:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ Десетцифрената стойност на ЕГН-то се криптира с публичен ключ (RSA public-key) от двойка ключове: публичен и частен;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ 10-те цифри се заменят с абревиатура: 'ЕГН' + хеш кода на криптираната стойност на ЕГН-то, по абсолютна стойност;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ Ако пред 10-те цифри стои абревиатура ЕГН, то тя се изтрива, а цифрите не се проверяват за валидно ЕГН, като:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ Десетцифрената стойност на ЕГН-то се криптира с публичен ключ (RSA public-key) от двойка ключове: публичен и частен;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ 10-те цифри се заменят с абревиатура: 'ЕГН' + хеш кода на криптираната стойност на ЕГН-то, по абсолютна стойност;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "";
        setDataGeneralStatisticsTextArea(taText);
        taText = " • CLIENT_RECEIVER_ACC:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Ако CLIENT_RECEIVER_NAME = 'Физическо лице', то CLIENT_RECEIVER_ACC се заменят с: абсолютната стойност на хеш кода на криптираната стойност (RSA public-key) на IBAN-a на Физическото лице.";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Ако CLIENT_RECEIVER_NAME <> 'Физическо лице', то CLIENT_RECEIVER_ACC не се променя.";
        setDataGeneralStatisticsTextArea(taText);
        taText = "";
        setDataGeneralStatisticsTextArea(taText);
        taText = " • REASON1, REASON2:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Проверява се за име на ФЛ - заменя се с: 'Физическо лице', само имената.";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Проверява се за ЕГН:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ Абревиатурата ЕГН се изтрива;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ 10-те цифри се проверяват за валидно ЕГН и ако то е такова:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ Десетцифрената стойност на ЕГН-то се криптира с публичен ключ (RSA public-key) от двойка ключове: публичен и частен;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ 10-те цифри се заменят с абревиатура: 'ЕГН' + хеш кода на криптираната стойност на ЕГН-то, по абсолютна стойност;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "     ⯈ Ако пред 10-те цифри стои абревиатура ЕГН, то тя се изтрива, а цифрите не се проверяват за валидно ЕГН, като:";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ Десетцифрената стойност на ЕГН-то се криптира с публичен ключ (RSA public-key) от двойка ключове: публичен и частен;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "         ⯮ 10-те цифри се заменят с абревиатура: 'ЕГН' + хеш кода на криптираната стойност на ЕГН-то, по абсолютна стойност;";
        setDataGeneralStatisticsTextArea(taText);
        taText = "   - Проверява се за адрес - заменя се със: 'Адрес', цялото поле.";
        setDataGeneralStatisticsTextArea(taText);
        taText = "------------------------------------------------------------------------------------------------------------------";
        setDataGeneralStatisticsTextArea(taText);
        slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=RED>1.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Входящ&nbsp;файл!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>2.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Файл&nbsp;с&nbsp;АО!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>3.</FONT></b>&nbsp;<b>Стартирайте:</b>&nbsp;<i><FONT COLOR=BLUE>Анонимизиране&nbsp;на&nbsp;данните!</FONT></i></html>";
        setStatusLabel(slText);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        generalSebraScrollPane = new javax.swing.JScrollPane();
        gsTextArea = new javax.swing.JTextArea();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        sebraMenuBar = new javax.swing.JMenuBar();
        menuChoiceFile = new javax.swing.JMenu();
        menuDeleteRegNum = new javax.swing.JMenu();
        choiceInDelRegNumFile = new javax.swing.JMenuItem();
        choiceRegNumFile = new javax.swing.JMenuItem();
        menuDeleteSebraCode = new javax.swing.JMenu();
        choiceInDelSebraCodesFile = new javax.swing.JMenuItem();
        choiceSebraCodesFile = new javax.swing.JMenuItem();
        menuInCsv = new javax.swing.JMenuItem();
        menuOrgCsv = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        About = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("  SEBRA Anonymization");
        setMaximumSize(new java.awt.Dimension(0, 0));
        setName("mainFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        mainPanel.setPreferredSize(new java.awt.Dimension(400, 340));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        generalSebraScrollPane.setMaximumSize(null);
        generalSebraScrollPane.setMinimumSize(null);

        gsTextArea.setEditable(false);
        gsTextArea.setColumns(20);
        gsTextArea.setRows(5);
        gsTextArea.setMaximumSize(null);
        gsTextArea.setMinimumSize(null);
        gsTextArea.setName(""); // NOI18N
        generalSebraScrollPane.setViewportView(gsTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(generalSebraScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(mainPanel, gridBagConstraints);

        statusPanel.setBackground(new java.awt.Color(250, 250, 250));
        statusPanel.setPreferredSize(new java.awt.Dimension(300, 30));
        statusPanel.setLayout(new java.awt.GridBagLayout());

        statusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel.setPreferredSize(new java.awt.Dimension(300, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        statusPanel.add(statusLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(statusPanel, gridBagConstraints);

        menuChoiceFile.setText("  Избор на файл ");
        menuChoiceFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuChoiceFile.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        menuChoiceFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuChoiceFileMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuChoiceFileMousePressed(evt);
            }
        });

        menuDeleteRegNum.setText("Изтриване по: Номер на регистрация");
        menuDeleteRegNum.setToolTipText("");
        menuDeleteRegNum.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        choiceInDelRegNumFile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        choiceInDelRegNumFile.setText("Избери: Файл за изтриване");
        choiceInDelRegNumFile.setToolTipText("");
        choiceInDelRegNumFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceInDelRegNumFileActionPerformed(evt);
            }
        });
        menuDeleteRegNum.add(choiceInDelRegNumFile);

        choiceRegNumFile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        choiceRegNumFile.setText("Избери: Файл с номера на регистрация");
        choiceRegNumFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceRegNumFileActionPerformed(evt);
            }
        });
        menuDeleteRegNum.add(choiceRegNumFile);

        menuChoiceFile.add(menuDeleteRegNum);

        menuDeleteSebraCode.setText("Изтриване по: Sebra Code");
        menuDeleteSebraCode.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        choiceInDelSebraCodesFile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        choiceInDelSebraCodesFile.setText("Избери: Файл за изтриване");
        choiceInDelSebraCodesFile.setToolTipText("");
        choiceInDelSebraCodesFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceInDelSebraCodesFileActionPerformed(evt);
            }
        });
        menuDeleteSebraCode.add(choiceInDelSebraCodesFile);

        choiceSebraCodesFile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        choiceSebraCodesFile.setText("Избери: Файл със Sebra Codes");
        choiceSebraCodesFile.setToolTipText("");
        choiceSebraCodesFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceSebraCodesFileActionPerformed(evt);
            }
        });
        menuDeleteSebraCode.add(choiceSebraCodesFile);

        menuChoiceFile.add(menuDeleteSebraCode);

        menuInCsv.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        menuInCsv.setText("Входящ файл");
        menuInCsv.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuInCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuInCsvActionPerformed(evt);
            }
        });
        menuChoiceFile.add(menuInCsv);

        menuOrgCsv.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        menuOrgCsv.setText("Файл с АО");
        menuOrgCsv.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuOrgCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOrgCsvActionPerformed(evt);
            }
        });
        menuChoiceFile.add(menuOrgCsv);

        sebraMenuBar.add(menuChoiceFile);

        jMenu1.setText("  |  ");
        jMenu1.setEnabled(false);
        sebraMenuBar.add(jMenu1);

        sebraMenuBar.add(Box.createHorizontalGlue());
        About.setText("  |   ");
        About.setEnabled(false);
        sebraMenuBar.add(About);

        menuAbout.setText("About  ");
        menuAbout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuAbout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        menuAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuAboutMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuAboutMousePressed(evt);
            }
        });
        sebraMenuBar.add(menuAbout);

        setJMenuBar(sebraMenuBar);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuInCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuInCsvActionPerformed
        // Selecting an incoming file to anonymize!
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                inCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                saltTxt = folder + "\\" + "salt" + ".txt";
                outCsv = folder + "\\" + "anonymized_" + onlyNameFile + ".csv";
                pathOutCsv = Paths.get(outCsv);
                this.setPathFolder(folder);
                this.setPathInCsv(inCsv);
                this.setPathSaltTxt(saltTxt);
                this.setPathOutCsv(outCsv);
                orgCsv = this.getPathOrgCsv();
                taText = " • Избран файл: " + inCsv + "!";
                setDataGeneralStatisticsTextArea(taText);
                slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inCsv + "</FONT></html>";
                setStatusLabel(slText);
                pathInCsv = Paths.get(inCsv);
                pathSaltTxt = Paths.get(saltTxt);
                msg = "<html>&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inCsv + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);

                if (!Files.exists(pathOutCsv)) {
                    Files.createFile(pathOutCsv);
                } else {
                    Object[] options = {"Да, моля", "Не, разбира се!"};
                    msg = "<html><center><b><FONT COLOR=RED>За избрания входящ файл, вече има генериран анонимизиран изходен файл!</FONT></b><br><FONT COLOR=BLUE>Желаете ли генериране на нов анонимизиран изходен файл и препокриване на съществуващия?</FONT></center></html>";
                    int num_opt = JOptionPane.showOptionDialog(f, msg, "Уместен въпрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    switch (num_opt) {
                        case JOptionPane.YES_OPTION:
                            break;
                        case JOptionPane.NO_OPTION:
                            msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                            JOptionPane.showMessageDialog(f, msg);
                            clearStatusLabel();
                            return;
                        default:
                            msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                            JOptionPane.showMessageDialog(f, msg);
                            clearStatusLabel();
                            return;
                    }
                }

                if (orgCsv.equals(null) || orgCsv.equals("")) {  // No Organizations file selected!  // Unable to start anonymization!
                    //
                } else {  // Organizations file selected!  // Anonymization can begin!
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избрани файлове:&nbsp;&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inCsv + "&nbsp;&nbsp;•&nbsp;" + orgCsv + "</FONT><html>";
                    setStatusLabel(slText);
                    pathOrgCsv = Paths.get(orgCsv);
                    Object[] options = {"Да, моля", "Няма начин!"};
                    msg = "<html><i><b><FONT COLOR=BLUE>Да започне ли анонимизацията на избрания файл?</FONT></b></i></html>";
                    int num_opt = JOptionPane.showOptionDialog(f, msg, "Уместен въпрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (num_opt == JOptionPane.YES_OPTION) {
                        makeAnonymization();
                    } else if (num_opt == JOptionPane.NO_OPTION) {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    } else {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    }
                }

                if (!Files.exists(pathSaltTxt)) {
                    Files.createFile(pathSaltTxt);
                }
                File file = new File(saltTxt);
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((salt = br.readLine()) != null) {
                }
                if (StringUtils.isBlank(salt)) {
                    salt = RandomStringUtils.randomAlphanumeric(300);
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] strToBytes = salt.getBytes();
                        outputStream.write(strToBytes);
                        outputStream.close();
                    } catch (IOException ex) {
                        error_text = ex.getMessage().toString();
                        msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                        JOptionPane.showMessageDialog(f, msg);
                    }
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_menuInCsvActionPerformed

    private void menuOrgCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOrgCsvActionPerformed
        // Select administrations file!
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                orgCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                this.setPathFolder(folder);
                this.setPathOrgCsv(orgCsv);
                inCsv = this.getPathInCsv();
                saltTxt = this.getPathSaltTxt();
                outCsv = this.getPathOutCsv();
                if (inCsv.equals(null) || inCsv.equals("") || outCsv.equals("") || outCsv.equals("")) {  // No Incoming File or Outgoing File selected!  // Unable to start anonymization!
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + orgCsv + "</FONT><html>";
                    setStatusLabel(slText);
                    pathOrgCsv = Paths.get(orgCsv);
                    msg = "<html>&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + orgCsv + "</FONT><html>";
                    JOptionPane.showMessageDialog(f, msg);
                } else {  // Selected Input and Output file!  // Anonymization can begin!
                    taText = " • Избран файл: " + orgCsv + "!";
                    setDataGeneralStatisticsTextArea(taText);
                    taText = "------------------------------------------------------------------------------------------------------------------";
                    setDataGeneralStatisticsTextArea(taText);
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избрани файлове:&nbsp;&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inCsv + "&nbsp;&nbsp;•&nbsp;" + orgCsv + "</FONT>";
                    setStatusLabel(slText);
                    pathInCsv = Paths.get(inCsv);
                    pathOrgCsv = Paths.get(orgCsv);
                    pathSaltTxt = Paths.get(saltTxt);
                    pathOutCsv = Paths.get(outCsv);
                    Object[] options = {"Да, моля", "Няма начин!"};
                    msg = "<html><i><b><FONT COLOR=BLUE>Да започне ли анонимизацията на избрания файл?</FONT></b></i></html>";
                    int num_opt = JOptionPane.showOptionDialog(f, msg, "Уместен въпрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (num_opt == JOptionPane.YES_OPTION) {
                        makeAnonymization();
                    } else if (num_opt == JOptionPane.NO_OPTION) {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    } else {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    }
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_menuOrgCsvActionPerformed

    private void menuChoiceFileMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuChoiceFileMousePressed
        slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=RED>1.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Входящ&nbsp;файл!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>2.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Файл&nbsp;с&nbsp;АО!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>3.</FONT></b>&nbsp;<b>Стартирайте:</b>&nbsp;<i><FONT COLOR=BLUE>Анонимизиране&nbsp;на&nbsp;данните!</FONT></i></html>";
        setStatusLabel(slText);
    }//GEN-LAST:event_menuChoiceFileMousePressed

    private void menuAboutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuAboutMousePressed
        slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN>&copy;&nbsp;</FONT></b><b><FONT COLOR=BLUE>2024 Ministry&nbsp;of&nbsp;e-Governance.&nbsp;All&nbsp;rights&nbsp;reserved.</FONT>&nbsp;&nbsp;<FONT COLOR=GREEN>Ver.1.05</FONT></b>&nbsp;&nbsp;</html>";
        setStatusLabel(slText);
    }//GEN-LAST:event_menuAboutMousePressed

    private void menuChoiceFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuChoiceFileMouseEntered
        slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=RED>1.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Входящ&nbsp;файл!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>2.</FONT></b>&nbsp;<b>Изберете:</b>&nbsp;<i><FONT COLOR=BLUE>Файл&nbsp;с&nbsp;АО!</FONT></i>" + "&nbsp;&nbsp;&nbsp;&nbsp;<b><FONT COLOR=RED>3.</FONT></b>&nbsp;<b>Стартирайте:</b>&nbsp;<i><FONT COLOR=BLUE>Анонимизиране&nbsp;на&nbsp;данните!</FONT></i></html>";
        setStatusLabel(slText);
    }//GEN-LAST:event_menuChoiceFileMouseEntered

    private void menuAboutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuAboutMouseEntered
        slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN>&copy;&nbsp;</FONT></b><b><FONT COLOR=BLUE>2024 Ministry&nbsp;of&nbsp;e-Governance.&nbsp;All&nbsp;rights&nbsp;reserved.</FONT>&nbsp;&nbsp;<FONT COLOR=GREEN>Ver.1.05</FONT></b>&nbsp;&nbsp;</html>";
        setStatusLabel(slText);
    }//GEN-LAST:event_menuAboutMouseEntered

    private void choiceInDelRegNumFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceInDelRegNumFileActionPerformed
        // Изтриване по: Номер на регистрация -> Избери: Файл за изтриване
        // Delete by: Registration number -> Select: File to delete
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                inDelRegNumCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                outDelRegNumCsv = folder + "\\" + "deleted_" + onlyNameFile + ".csv";
                pathOutDelRegNumCsv = Paths.get(outDelRegNumCsv);
                rejectedDelRegNumCsv = folder + "\\" + "rejected_" + onlyNameFile + ".csv";
                pathRejectedDelRegNumCsv = Paths.get(rejectedDelRegNumCsv);
                autodelRegNumCsv = folder + "\\" + "autodel_" + onlyNameFile + ".csv";
                pathAutodelRegNumCsv = Paths.get(autodelRegNumCsv);

                this.setPathFolder(folder);
                this.setPathInDelRegNumCsv(inDelRegNumCsv);
                this.setPathOutDelRegNumCsv(outDelRegNumCsv);
                this.setPathRejectedDelRegNumCsv(rejectedDelRegNumCsv);
                this.setPathAutodelRegNumCsv(autodelRegNumCsv);

                taText = " • Избран файл: " + inDelRegNumCsv + "!";
                setDataGeneralStatisticsTextArea(taText);
                slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelRegNumCsv + "</FONT></html>";
                setStatusLabel(slText);
                pathInCsv = Paths.get(inDelRegNumCsv);
                msg = "<html>&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelRegNumCsv + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);

                if (!Files.exists(pathOutDelRegNumCsv)) {
                    Files.createFile(pathOutDelRegNumCsv);
                }
                if (!Files.exists(pathRejectedDelRegNumCsv)) {
                    Files.createFile(pathRejectedDelRegNumCsv);
                }
                if (!Files.exists(pathAutodelRegNumCsv)) {
                    Files.createFile(pathAutodelRegNumCsv);
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_choiceInDelRegNumFileActionPerformed

    private void choiceRegNumFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceRegNumFileActionPerformed
        // Изтриване по: Номер на регистрация -> Избери: Файл с номера на регистрация
        // Delete by: Registration Number -> Select: File with Registration Number
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                regNumCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                this.setPathFolder(folder);
                this.setPathRegNumCsv(regNumCsv);
                inDelRegNumCsv = this.getPathInDelRegNumCsv();
                outDelRegNumCsv = this.getPathOutDelRegNumCsv();
                rejectedDelRegNumCsv = this.getPathRejectedDelRegNumCsv();
                autodelRegNumCsv = this.getPathAutodelRegNumCsv();

                if (inDelRegNumCsv.equals(null) || inDelRegNumCsv.equals("") || outDelRegNumCsv.equals("") || outDelRegNumCsv.equals("")) {  // No Incoming File or Outgoing File selected!  // Unable to start deletion!
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=RED></FONT><FONT COLOR=RED>Няма избран файл за изтриване по номер на регистрация!:&nbsp;</FONT></b><html>";
                    setStatusLabel(slText);
                    pathRegNumCsv = Paths.get(regNumCsv);
                    msg = "<html>&nbsp;<b><FONT COLOR=RED></FONT><FONT COLOR=RED>Няма избран файл за изтриване по номер на регистрация!&nbsp;</FONT></b><html>";
                    JOptionPane.showMessageDialog(f, msg);
                } else {  // Selected Input and Output file!  // Anonymization can begin!
                    taText = " • Избран файл: " + regNumCsv + "!";
                    setDataGeneralStatisticsTextArea(taText);
                    taText = "------------------------------------------------------------------------------------------------------------------";
                    setDataGeneralStatisticsTextArea(taText);
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избрани файлове:&nbsp;&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelRegNumCsv + "&nbsp;&nbsp;•&nbsp;" + regNumCsv + "</FONT>";
                    setStatusLabel(slText);
                    pathInDelRegNumCsv = Paths.get(inDelRegNumCsv);
                    pathRegNumCsv = Paths.get(regNumCsv);
                    pathOutDelRegNumCsv = Paths.get(outDelRegNumCsv);
                    pathRejectedDelRegNumCsv = Paths.get(rejectedDelRegNumCsv);
                    pathAutodelRegNumCsv = Paths.get(autodelRegNumCsv);

                    Object[] options = {"Да, моля", "Няма начин!"};
                    msg = "<html><i><b><FONT COLOR=BLUE>Да започне ли изтриването на избрания файл?</FONT></b></i></html>";
                    int num_opt = JOptionPane.showOptionDialog(f, msg, "Уместен въпрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (num_opt == JOptionPane.YES_OPTION) {
                        makeDeletionByRegistrationNumber();
                    } else if (num_opt == JOptionPane.NO_OPTION) {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    } else {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    }
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_choiceRegNumFileActionPerformed

    private void choiceInDelSebraCodesFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceInDelSebraCodesFileActionPerformed
        // Изтриване по: Sebra Code -> Избери: Файл за изтриване
        // Delete by: Sebra Code -> Select: File to delete
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                inDelSebraCodesCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                outDelSebraCodesCsv = folder + "\\" + "deleted_" + onlyNameFile + ".csv";
                pathOutDelSebraCodesCsv = Paths.get(outDelSebraCodesCsv);
                autodelDelSebraCodesCsv = folder + "\\" + "autodel_" + onlyNameFile + ".csv";
                pathAutodelSebraCodesCsv = Paths.get(autodelDelSebraCodesCsv);
                this.setPathFolder(folder);
                this.setPathInDelSebraCodesCsv(inDelSebraCodesCsv);
                this.setPathOutDelSebraCodesCsv(outDelSebraCodesCsv);
                this.setPathAutodelSebraCodesCsv(autodelDelSebraCodesCsv);

                taText = " • Избран файл: " + inDelSebraCodesCsv + "!";
                setDataGeneralStatisticsTextArea(taText);
                slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelSebraCodesCsv + "</FONT></html>";
                setStatusLabel(slText);
                pathInCsv = Paths.get(inDelSebraCodesCsv);
                msg = "<html>&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избран файл:&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelSebraCodesCsv + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);

                if (!Files.exists(pathOutDelSebraCodesCsv)) {
                    Files.createFile(pathOutDelSebraCodesCsv);
                }
                if (!Files.exists(pathAutodelSebraCodesCsv)) {
                    Files.createFile(pathAutodelSebraCodesCsv);
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_choiceInDelSebraCodesFileActionPerformed

    private void choiceSebraCodesFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceSebraCodesFileActionPerformed
        // Изтриване по: Sebra Code -> Избери: Файл със Sebra Codes
        // Delete by: Sebra Code -> Select: File with Sebra Codes
        File selectedFile = null;
        String nameFile = null;
        String onlyNameFile = null;
        String msg = null;
        String error_text = null;
        JFileChooser fileChooser = null;
        folder = this.getPathFolder();
        if (folder.equals(null) || folder.equals("")) {
            folder = System.getProperty("user.home").toString();
        }
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(folder));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv", "CSV"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                selectedFile = fileChooser.getSelectedFile();
                sebraCodesCsv = selectedFile.getAbsolutePath();
                nameFile = selectedFile.getName();
                folder = selectedFile.getParent();
                String[] res = nameFile.split("[.]", 0);
                onlyNameFile = res[0];
                this.setPathFolder(folder);
                this.setPathSebraCodesCsv(sebraCodesCsv);
                inDelSebraCodesCsv = this.getPathInDelSebraCodesCsv();
                outDelSebraCodesCsv = this.getPathOutDelSebraCodesCsv();
                autodelDelSebraCodesCsv = this.getPathAutodelSebraCodesCsv();

                if (inDelSebraCodesCsv.equals(null) || inDelSebraCodesCsv.equals("") || outDelSebraCodesCsv.equals("") || outDelSebraCodesCsv.equals("")) {  // No Incoming File or Outgoing File selected!  // Unable to start deletion!
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=RED></FONT><FONT COLOR=RED>Няма избран файл за изтриване по себра код!:&nbsp;</FONT></b><html>";
                    setStatusLabel(slText);
                    pathRegNumCsv = Paths.get(sebraCodesCsv);
                    msg = "<html>&nbsp;<b><FONT COLOR=RED></FONT><FONT COLOR=RED>Няма избран файл за изтриване по себра код!&nbsp;</FONT></b><html>";
                    JOptionPane.showMessageDialog(f, msg);
                } else {  // Selected Input and Output file!  // Anonymization can begin!
                    taText = " • Избран файл: " + sebraCodesCsv + "!";
                    setDataGeneralStatisticsTextArea(taText);
                    taText = "------------------------------------------------------------------------------------------------------------------";
                    setDataGeneralStatisticsTextArea(taText);
                    slText = "<html>&nbsp;&nbsp;<b><FONT COLOR=GREEN></FONT><FONT COLOR=RED>Избрани файлове:&nbsp;&nbsp;</FONT></b><FONT COLOR=BLUE>•&nbsp;" + inDelSebraCodesCsv + "&nbsp;&nbsp;•&nbsp;" + sebraCodesCsv + "</FONT>";
                    setStatusLabel(slText);
                    pathInDelSebraCodesCsv = Paths.get(inDelSebraCodesCsv);
                    pathSebraCodesCsv = Paths.get(sebraCodesCsv);
                    pathOutDelSebraCodesCsv = Paths.get(outDelSebraCodesCsv);
                    pathAutodelSebraCodesCsv = Paths.get(autodelDelSebraCodesCsv);

                    Object[] options = {"Да, моля", "Няма начин!"};
                    msg = "<html><i><b><FONT COLOR=BLUE>Да започне ли изтриването на избрания файл?</FONT></b></i></html>";
                    int num_opt = JOptionPane.showOptionDialog(f, msg, "Уместен въпрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (num_opt == JOptionPane.YES_OPTION) {
                        makeDeletionBySebraCode();
                    } else if (num_opt == JOptionPane.NO_OPTION) {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    } else {
                        msg = "<html><i><b><FONT COLOR=BLUE>Отказът Ви е одобрен!</FONT></b></i></html>";
                        JOptionPane.showMessageDialog(f, msg);
                        clearStatusLabel();
                    }
                }

                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception ex) {
                Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                error_text = ex.getMessage().toString();
                msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                JOptionPane.showMessageDialog(f, msg);
                clearStatusLabel();
            } finally {
                try {
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
                    error_text = ex.getMessage().toString();
                    msg = "<html><FONT COLOR=RED><b>Съжаляваме, възникна грешка:&nbsp;</b></FONT><FONT COLOR=BLUE>" + error_text + "</FONT></html>";
                    JOptionPane.showMessageDialog(f, msg);
                    clearStatusLabel();
                }
            }
        }
    }//GEN-LAST:event_choiceSebraCodesFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Sebra().setVisible(true);
            }
        });
    }

    public void makeAnonymization() {
        inCsv = getPathInCsv();  // Incoming file
        salt = getSalt();  // Using to create Hash Code
        orgCsv = getPathOrgCsv();  // File with administrative authorities
        outCsv = getPathOutCsv();  // Outgoing file
        String msg = null;
        String untranslitaratedBeneficiary = "";
        String beneficiary = "";
        String iban = "";
        String reason1 = "";
        String reason2 = "";

        Map<String, List<Organization>> organizations = new HashMap<>();  // CODE,NAME,DESCR,DATE_FROM,DATE_TO
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(orgCsv)), "utf-8")) {  // org.csv | (new FileInputStream(orgCsv)), "cp1251")
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                List<Organization> list = organizations.computeIfAbsent(record.get(0), (k) -> new ArrayList<>());  // // Add a new key-value pair only if the key does not exist in the HashMap, or is mapped to `null`.
                Organization org = new Organization();
                org.setCode(record.get(0));
                String name = record.get(1);
                String description = record.get(2);
                if (description.length() > name.length()) {
                    org.setName(description);
                } else {
                    org.setName(name);
                }
                org.setFrom(LocalDate.parse(record.get(3).trim().split("\\s+")[0], DATE_FORMAT).atStartOfDay().minusDays(1));
                if (record.get(4).endsWith("3333")) {
                    org.setTo(LocalDateTime.now().plusWeeks(1));
                } else {
                    org.setTo(LocalDate.parse(record.get(4), DATE_FORMAT).atStartOfDay().plusDays(1));
                }
                list.add(org);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(inCsv)), "utf-8"); OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outCsv)), StandardCharsets.UTF_8)) {  // (new BufferedOutputStream(new FileOutputStream(outCsv)), StandardCharsets.UTF_8)  // (new BufferedOutputStream(new FileOutputStream(outCsv)), "UTF-8") // (new FileInputStream(inCsv)), "cp1251")
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                int anonymizedReasons = 0;
                int anonymizedReceivers = 0;
                int anonymizedClassified = 0;
                boolean header = true;

                for (CSVRecord record : parser) {
                    try {
                        iban = record.get(2);  // CLIENT_RECEIVER_ACC
                        reason1 = record.get(8);  // REASON1
                        reason2 = record.get(9);  // REASON2

                        if (isClassifiedInformation(reason1) || isClassifiedInformation(reason2)) {
                            anonymizedClassified++;
                            continue;
                        }

                        List<String> row = new ArrayList<>();
                        for (int i = 0; i < record.size(); i++) {
                            row.add(record.get(i).trim());
                        }

                        untranslitaratedBeneficiary = untransliterate(record.get(1));  // CLIENT_RECEIVER_NAME
                        beneficiary = anonymizeReceivers(untranslitaratedBeneficiary);
                        iban = anonymizeReceiversIban(iban, beneficiary);
                        reason1 = anonymizeReasons(reason1);
                        reason2 = anonymizeReasons(reason2);

                        row.set(1, beneficiary);
                        row.set(2, iban);
                        row.set(8, reason1);
                        row.set(9, reason2);

                        if (!beneficiary.equalsIgnoreCase(record.get(1))) {
                            anonymizedReceivers++;
                        }
                        if (!reason1.equalsIgnoreCase(record.get(8)) || !reason2.equalsIgnoreCase(record.get(9))) {
                            anonymizedReasons++;
                        }

                        if (header) {
                            row.add("ORGANIZATION");
                            row.add("PRIMARY_ORGANIZATION");
                            row.add("PRIMARY_ORG_CODE");
                            row.add("CLIENT_NAME_HASH");
                        } else {
                            List<Organization> orgs = organizations.get(record.get(4));  // key = FIN_CODE
                            if (orgs != null) {
                                if (orgs.size() == 1) {
                                    Organization org = orgs.get(0);
                                    row.add(org.getName());
                                } else {
                                    boolean foundOrganization = false;
                                    for (Organization org : orgs) {
                                        LocalDateTime txDate = LocalDate.parse(record.get(0), DATE_FORMAT).atStartOfDay();  // SETTLEMENT_DATE
                                        if (txDate.isAfter(org.getFrom()) && txDate.isBefore(org.getTo())) {  // SETTLEMENT_DATE > DATE_FROM AND SETTLEMENT_DATE < DATE_TO
                                            row.add(org.getName());
                                            foundOrganization = true;
                                            break;
                                        }
                                    }
                                    if (!foundOrganization) {
                                        row.add(orgs.get(orgs.size() - 1).getName());
                                    }
                                }
                            }

                            String primaryOrganizationCode = record.get(4).substring(0, 3);  // Add primary organization
                            List<Organization> primaryOrgs = organizations.get(primaryOrganizationCode + StringUtils.repeat("*", 7));
                            if (primaryOrgs != null) {
                                boolean foundOrganization = false;
                                for (Organization org : primaryOrgs) {
                                    LocalDateTime txDate = LocalDate.parse(record.get(0), DATE_FORMAT).atStartOfDay();
                                    if (txDate.isAfter(org.getFrom()) && txDate.isBefore(org.getTo())) {
                                        row.add(org.getName());
                                        foundOrganization = true;
                                        break;
                                    }
                                }
                                if (!foundOrganization) {
                                    row.add(primaryOrgs.get(primaryOrgs.size() - 1).getName());
                                }
                            } else {
                                row.add("");
                            }
                            row.add(primaryOrganizationCode);
                            row.add(bytesToHex(DIGEST.digest((salt + beneficiary.replaceAll("\\s+", " ")).getBytes(StandardCharsets.UTF_8))));  // Hash a salt + normalized, untransliterated, lowercase beneficary name
                        }
                        printer.printRecord(row);
                        header = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                taText = " Анонимизирането завърши успешно!";
                setDataGeneralStatisticsTextArea(taText);
                taText = " • Анонимизирани основания за плащане: " + String.valueOf(anonymizedReasons);
                setDataGeneralStatisticsTextArea(taText);
                taText = " • Анонимизирани получатели: " + String.valueOf(anonymizedReceivers);
                setDataGeneralStatisticsTextArea(taText);
                taText = " • Класифицирани записи: " + String.valueOf(anonymizedClassified);
                setDataGeneralStatisticsTextArea(taText);
                taText = "------------------------------------------------------------------------------------------------------------------";
                setDataGeneralStatisticsTextArea(taText);
                taText = " • Проверка за коректна анонимизация.";
                setDataGeneralStatisticsTextArea(taText);
                taText = "   В анинимизирания файл не трябва да има:";
                setDataGeneralStatisticsTextArea(taText);
                taText = "   - Адреси:";
                setDataGeneralStatisticsTextArea(taText);
                taText = "       ⯈ Търсим в получения файл: 'бул.', 'ул.', 'бл.' и проверяваме дали в анонимизирания файл полето е заместено с: 'Адрес'.";
                setDataGeneralStatisticsTextArea(taText);
                taText = "   - ЕГН:";
                setDataGeneralStatisticsTextArea(taText);
                taText = "       ⯈ Търсим в анонимизирания файл абревиатура 'ЕГН' и проверяваме дали след нея стои хеш кода на криптираната стойност на ЕГН-то, по абсолютна стойност, а в получения файл проверяваме за наличие на абревиатура 'ЕГН' и десетцифреното ЕГН, или за наличие само на десетцифрено ЕГН.";
                setDataGeneralStatisticsTextArea(taText);
                taText = "   - Имена на Физически лица:";
                setDataGeneralStatisticsTextArea(taText);
                taText = "       ⯈ Търсим в анонимизирания файл израза 'Физическо лице' и проверяваме дали в получения файл действително стоят имената на физическо лице или са други думи, разпознати като имена на хора.";
                setDataGeneralStatisticsTextArea(taText);
                taText = "   - IBAN на бенефициент ФЛ:";
                setDataGeneralStatisticsTextArea(taText);
                taText = "       ⯈ Търсим в анонимизирания файл израза 'Физическо лице' за поле: 'CLIENT_RECEIVER_NAME' и проверяваме дали за тези записи поле: 'CLIENT_RECEIVER_ACC' = 'абсолютната стойност на хеш кода на криптираната стойност (RSA public-key) на IBAN-a на Физическото лице'.";
                setDataGeneralStatisticsTextArea(taText);
                taText = "------------------------------------------------------------------------------------------------------------------";
                setDataGeneralStatisticsTextArea(taText);
                slText = "<html><FONT COLOR=GREEN><b>&nbsp;&nbsp;Анонимизирането завърши успешно:</b></FONT><FONT COLOR=BLUE>&nbsp;&nbsp;&nbsp;&nbsp;•&nbsp;Анонимизирани записи:&nbsp;" + (anonymizedReasons + anonymizedReceivers) + "</FONT></FONT><FONT COLOR=RED>&nbsp;&nbsp;&nbsp;&nbsp;•&nbsp;Класифицирани записи:&nbsp;" + anonymizedClassified + "</FONT></html>";
                setStatusLabel(slText);
                msg = "<html><center><FONT COLOR=GREEN><b>Анонимизирането завърши успешно!</b><br></FONT><FONT COLOR=BLUE>•&nbsp;Анонимизирани основания за плащане:&nbsp;" + anonymizedReasons + "<br>•&nbsp;Анонимизирани получатели:&nbsp;" + anonymizedReceivers + "</FONT><br></FONT><FONT COLOR=RED>•&nbsp;Класифицирани записи:&nbsp;" + anonymizedClassified + "</FONT></center></html>";
                JOptionPane.showMessageDialog(f, msg);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isClassifiedInformation(String reason) {
        reason = reason.toLowerCase();
        if ((reason.contains("класифиц") && !reason.contains("некласифиц"))
                || reason.contains("секретн") || reason.contains("секрете")
                || reason.contains("поверит") || reason.contains("ззки")) {
            return true;
        }
        return false;
    }

    private String anonymizeReasons(String reason) {
        String result = "";
        String[] parts = {};
        String lowerCaseString = reason.toLowerCase().trim();
        String msg = null;
        parts = lowerCaseString.split("\\s+");
        Boolean isHasAddress = false;
        Boolean isHasBulstat = false;
        Boolean isHasEgn = false;
        Boolean isExpressionPersonalName = false;
        Boolean isPartOnlyString = false;
        Boolean isPartOnlyDigits = false;
        Boolean isPartPersonalName = false;
        Boolean isPartValidEgn = false;
        Boolean isPartCombineEgn = false;
        Boolean isPartCombineBulstat = false;
        Boolean isPartCombineStringDigits = false;
        Boolean isTextBulstat = false;
        Boolean isPersonalName = false;
        Boolean isTextEgn = false;
        Boolean isExpressionNonPersonalIndicators = false;
        String partsPersonalName = "";
        int countPersonalName = 0;
        int countPartOnlyDigits = 0;
        int countPartStringDigits = 0;
        int lengthPartsPersonalName = 0;
        int lengthResult = 0;
        String encryptEgn = "";
        String hashEncryptEgn = "";
        String resultPart = "";

        isHasAddress = checkIsHasAddress(lowerCaseString);
        isHasBulstat = checkIsHasBulstat(lowerCaseString);
        isHasEgn = checkIsHasEgn(lowerCaseString);
        isExpressionPersonalName = checkIsExpressionPersonalName(lowerCaseString);
        isExpressionNonPersonalIndicators = checkIsExpressionNonPersonalIndicators(lowerCaseString);

        System.out.println("1. isHasAddress: " + isHasAddress + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isHasBulstat: " + isHasBulstat + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isHasEgn: " + isHasEgn + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isExpressionPersonalName: " + isExpressionPersonalName + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isExpressionNonPersonalIndicators: " + isExpressionNonPersonalIndicators + " | Expression: " + lowerCaseString + "");

        for (String part : parts) {
            if (isHasAddress == true) {  // The expression contain an Address - Replace with 'Адрес'!
                result = "Адрес";
                System.out.println("2. isHasAddress: " + isHasAddress + " | " + part + "");
            } else {  // The expression does NOT contain an Address!
                if (isExpressionNonPersonalIndicators == true) {  // Expression contains non personal indicators, i.e. the parts recognized as personal names should be recorded!
                    isPartOnlyDigits = checkIsPartOnlyDigits(part);
                    if (isPartOnlyDigits == true) {  // Word is Only Digits
                        if (isHasEgn == true && isHasBulstat == false) {  // If the expression contains word EGN AND does not contain word Bulstat!
                            isPartValidEgn = checkIsPartValidEgn(part);
                            if (isPartValidEgn == true) {  // Word is valid EGN!
                                encryptEgn = encryptionEgn(part);
                                hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                result += "ЕГН " + hashEncryptEgn + " ";
                                System.out.println("3.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                            } else {  // Word isn't valid EGN!
                                if (part.length() == 10) {
                                    encryptEgn = encryptionEgn(part);
                                    hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                    result += " " + hashEncryptEgn + " ";
                                    System.out.println("3.2. isPartOnlyDigits: " + isPartOnlyDigits + " | IF (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                } else {
                                    result += part + " ";
                                    System.out.println("3.3. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                }
                            }  // Is valid EGN?
                        } else {  // Word isn't valid EGN! Is the word is Bulstat - not change it!
                            result += part + " ";
                            System.out.println("3.4. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE isHasEgn " + " | part: " + part + " | result: " + result + "");
                        }  // Contains the EGN/Bulstat?
                    } else {  // Word does NOT Only Digits
                        if (isHasBulstat == true) {  // If the expression contains word Bulstat!
                            isTextBulstat = checkIsTextBulstat(part);
                            if (isTextBulstat == true) {  // Word is Bulstat!
                                result += "Булстат" + " ";
                                System.out.println("4.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                            } else {  // Word isn't Bulstat!
                                result += part + " ";
                                System.out.println("4.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                            }  // Is word Bulstat?
                        } else {  // If the expression doesn't contains word Bulstat!
                            isPartCombineEgn = checkIsPartCombineEgn(part);
                            if (isPartCombineEgn == true) {  // The word is a combination of EGN as 'ЕГН0123456789'!
                                resultPart = getPartDigit(part);
                                encryptEgn = encryptionEgn(resultPart);
                                hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                result += "ЕГН " + hashEncryptEgn + " ";
                                System.out.println("4.3. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartCombineEgn " + isPartCombineEgn + " | part: " + part + " | result: " + result + "");
                            } else {  // The word isn't combination of EGN!
                                isTextEgn = checkIsTextEgn(part);
                                if (isTextEgn == true) {  // Word is Egn!
                                    result += "";
                                    System.out.println("4.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                } else {
                                    result += part + " ";
                                    System.out.println("4.5. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                }  // Is Egn?
                            }  // Is word  a combination of EGN?
                        }  // Is contains word Bulstat?
                    }  // Is Only Digits?
                } else {  // Expression isn't contains non personal indicators!
                    isPartOnlyString = checkIsPartOnlyString(part);
                    if (isPartOnlyString == true) {  // Word Only String
                        isPartPersonalName = checkIsPartPersonalName(part);
                        if (isPartPersonalName == true) {  // Words is PersonalName
                            if (isExpressionPersonalName == true) {
                                isPersonalName = isPartPersonalName;
                                countPersonalName++;
                                partsPersonalName += part + " ";
                                System.out.println("5.1. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                            }
                        } else {  // Words isn't PersonalName
                            if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                                isPersonalName = false;
                                result += "Физическо лице" + " ";
                                System.out.println("5.2. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                partsPersonalName = "";
                            } else {
                                isTextEgn = checkIsTextEgn(part);
                                if (isTextEgn == true) {  // Word is Egn!
                                    result += "";
                                    System.out.println("5.3. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                } else {
                                    result += part + " ";
                                    System.out.println("5.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                }  // Is Egn?
                                partsPersonalName = "";
                            }
                        }  // Is PersonalName?
                    } else {  // Word Not Only String
                        if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                            isPersonalName = false;
                            result += "Физическо лице" + " ";
                            partsPersonalName = "";
                            System.out.println("6.1. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | part: " + part + " | result: " + result + "");
                        } else {  // If the previous words aren't PersonalName
                            isPartOnlyDigits = checkIsPartOnlyDigits(part);
                            if (isPartOnlyDigits == true) {  // Word is Only Digits
                                if (isHasEgn == true && isHasBulstat == false) {  // If the expression contains word EGN AND does not contain word Bulstat!
                                    isPartValidEgn = checkIsPartValidEgn(part);
                                    if (isPartValidEgn == true) {  // Word is valid EGN!
                                        encryptEgn = encryptionEgn(part);
                                        hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                        result += "ЕГН " + hashEncryptEgn + " ";
                                        System.out.println("6.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't valid EGN!
                                        if (part.length() == 10) {
                                            encryptEgn = encryptionEgn(part);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += " " + hashEncryptEgn + " ";
                                            System.out.println("6.3. isPartOnlyDigits: " + isPartOnlyDigits + " | IF (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                        } else {
                                            result += part + " ";
                                            System.out.println("6.4. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                        }
                                    }  // Is valid EGN?
                                } else {  // Word isn't valid EGN! Is the word is Bulstat - not change it!
                                    isPartValidEgn = checkIsPartValidEgn(part);
                                    if (isPartValidEgn == true) {  // Word is valid EGN!
                                        encryptEgn = encryptionEgn(part);
                                        hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                        result += "ЕГН " + hashEncryptEgn + " ";
                                        System.out.println("6.5. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't valid EGN!
                                        result += part + " ";
                                        countPartOnlyDigits++;
                                        System.out.println("6.6. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                    }  // Is valid EGN?
                                }  // Contains the EGN/Bulstat?
                            } else {  // Word does NOT Only Digits
                                if (isHasBulstat == true) {  // If the expression contains word Bulstat!
                                    isTextBulstat = checkIsTextBulstat(part);
                                    if (isTextBulstat == true) {  // Word is Bulstat!
                                        result += "Булстат" + " ";
                                        System.out.println("7.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't Bulstat!
                                        result += part + " ";
                                        System.out.println("7.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                    }  // Is word Bulstat?
                                } else {  // If the expression doesn't contains word Bulstat!
                                    isPartCombineStringDigits = checkIsPartCombineStringDigits(part);
                                    if (isPartCombineStringDigits == true) {  // The word has combine string and digits
                                        countPartStringDigits++;
                                        isPartCombineEgn = checkIsPartCombineEgn(part);
                                        if (isPartCombineEgn == true) {  // The word is a combination of EGN as 'ЕГН0123456789'!
                                            resultPart = getPartDigit(part);
                                            encryptEgn = encryptionEgn(resultPart);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += "ЕГН " + hashEncryptEgn + " ";
                                            System.out.println("7.3. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartCombineEgn " + isPartCombineEgn + " | part: " + part + " | result: " + result + "");
                                        } else {  // The word hasn't combine string and digits
                                            result += part + " ";
                                            System.out.println("7.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                        }  // Is word a combination of EGN?
                                        System.out.println("7.5. isPartCombineStringDigits: " + isPartCombineStringDigits + " | countPartStringDigits: " + countPartStringDigits + " | part: " + part + " | result: " + result + "");
                                    } else {  // The word hasn't combine string and digits
                                        isTextEgn = checkIsTextEgn(part);
                                        if (isTextEgn == true) {  // Word is Egn!
                                            result += "";
                                            System.out.println("7.6. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                        } else {
                                            isPartPersonalName = checkIsPartPersonalName(part);
                                            if (isPartPersonalName == true) {  // Words is PersonalName
                                                if (isExpressionPersonalName == true) {
                                                    isPersonalName = isPartPersonalName;
                                                    countPersonalName++;
                                                    partsPersonalName += part + " ";
                                                    System.out.println("8.1. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                }
                                            } else {  // Words isn't PersonalName
                                                if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                                                    isPersonalName = false;
                                                    result += "Физическо лице" + " ";
                                                    System.out.println("8.2. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                    partsPersonalName = "";
                                                } else {  // If the previous words aren't PersonalName
                                                    result += part + " ";
                                                    System.out.println("8.3. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                                }  // Was there a PersonalName in the previous words?
                                            }  // Is PersonalName?
                                        }  // Is Egn?
                                    }  // Is there a combined string and digits
                                }  // Is contains word Bulstat?
                            }  // Is Only Digits?
                        }  // Is previous words are PersonalName?
                    }  // Is word Only String?
                }  // Is expression contains non personal indicators?
            }  // Is contain an Address?
        }  // for

        lengthPartsPersonalName = partsPersonalName.length();
        lengthResult = result.length();
        if (lengthPartsPersonalName > 0) {
            if (lengthResult > 0) {
                if (countPersonalName >= 2) {
                    result = "Физическо лице" + " ";
                    System.out.println("9.1. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                } else {
                    result += partsPersonalName.trim() + " ";
                    System.out.println("9.2. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                }
            } else {
                if (countPersonalName >= 2) {
                    result = "Физическо лице" + " ";
                    System.out.println("9.3. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                } else {
                    result += partsPersonalName.trim() + " ";
                    System.out.println("9.4. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                }
            }
            partsPersonalName = "";
        }

        result = result.trim().toUpperCase();
        System.out.println("10. result: " + result + "");
        return result;
    }

    private String anonymizeReceivers(String reason) {
        String result = "";
        String[] parts = {};
        String lowerCaseString = reason.toLowerCase().trim();
        String msg = null;
        parts = lowerCaseString.split("\\s+");
        Boolean isHasBulstat = false;
        Boolean isHasEgn = false;
        Boolean isExpressionPersonalName = false;
        Boolean isPartOnlyString = false;
        Boolean isPartOnlyDigits = false;
        Boolean isPartPersonalName = false;
        Boolean isPartValidEgn = false;
        Boolean isPartCombineEgn = false;
        Boolean isPartCombineBulstat = false;
        Boolean isTextBulstat = false;
        Boolean isPersonalName = false;
        Boolean isTextEgn = false;
        Boolean isExpressionNonPersonalIndicators = false;
        String partsPersonalName = "";
        int countPersonalName = 0;
        int lengthPartsPersonalName = 0;
        int lengthResult = 0;
        String encryptEgn = "";
        String hashEncryptEgn = "";
        String resultPart = "";

        isHasBulstat = checkIsHasBulstat(lowerCaseString);
        isHasEgn = checkIsHasEgn(lowerCaseString);
        isExpressionPersonalName = checkIsExpressionPersonalName(lowerCaseString);
        isExpressionNonPersonalIndicators = checkIsExpressionNonPersonalIndicators(lowerCaseString);

        System.out.println("1. isHasBulstat: " + isHasBulstat + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isHasEgn: " + isHasEgn + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isExpressionPersonalName: " + isExpressionPersonalName + " | Expression: " + lowerCaseString + "");
        System.out.println("1. isExpressionNonPersonalIndicators: " + isExpressionNonPersonalIndicators + " | Expression: " + lowerCaseString + "");

        for (String part : parts) {
            if (isExpressionNonPersonalIndicators == true) {  // Expression contains non personal indicators, i.e. the parts recognized as personal names should be recorded!
                isPartOnlyDigits = checkIsPartOnlyDigits(part);
                if (isPartOnlyDigits == true) {  // Word is Only Digits
                    if (isHasEgn == true && isHasBulstat == false) {  // If the expression contains word EGN AND does not contain word Bulstat!
                        isPartValidEgn = checkIsPartValidEgn(part);
                        if (isPartValidEgn == true) {  // Word is valid EGN!
                            encryptEgn = encryptionEgn(part);
                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                            result += "ЕГН " + hashEncryptEgn + " ";
                            System.out.println("3.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                        } else {  // Word isn't valid EGN!
                            if (part.length() == 10) {
                                encryptEgn = encryptionEgn(part);
                                hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                result += " " + hashEncryptEgn + " ";
                                System.out.println("3.2. isPartOnlyDigits: " + isPartOnlyDigits + " | IF (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                            } else {
                                result += part + " ";
                                System.out.println("3.3. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                            }
                        }  // Is valid EGN?
                    } else {  // Word isn't valid EGN! Is the word is Bulstat - not change it!
                        result += part + " ";
                        System.out.println("3.4. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE isHasEgn " + " | part: " + part + " | result: " + result + "");
                    }  // Contains the EGN/Bulstat?
                } else {  // Word does NOT Only Digits
                    if (isHasBulstat == true) {  // If the expression contains word Bulstat!
                        isTextBulstat = checkIsTextBulstat(part);
                        if (isTextBulstat == true) {  // Word is Bulstat!
                            result += "Булстат" + " ";
                            System.out.println("4.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                        } else {  // Word isn't Bulstat!
                            result += part + " ";
                            System.out.println("4.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                        }  // Is word Bulstat?
                    } else {  // If the expression doesn't contains word Bulstat!
                        isPartCombineEgn = checkIsPartCombineEgn(part);
                        if (isPartCombineEgn == true) {  // The word is a combination of EGN as 'ЕГН0123456789'!
                            resultPart = getPartDigit(part);
                            encryptEgn = encryptionEgn(resultPart);
                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                            result += "ЕГН " + hashEncryptEgn + " ";
                            System.out.println("4.3. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartCombineEgn " + isPartCombineEgn + " | part: " + part + " | result: " + result + "");
                        } else {  // The word isn't combination of EGN!
                            isTextEgn = checkIsTextEgn(part);
                            if (isTextEgn == true) {  // Word is Egn!
                                result += "";
                                System.out.println("4.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                            } else {
                                result += part + " ";
                                System.out.println("4.5. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                            }  // Is Egn?
                        }  // Is word  a combination of EGN?
                    }  // Is contains word Bulstat?
                }  // Is Only Digits?
            } else {  // Expression isn't contains non personal indicators!
                isPartOnlyString = checkIsPartOnlyString(part);
                if (isPartOnlyString == true) {  // Word Only String
                    isPartPersonalName = checkIsPartPersonalName(part);
                    if (isPartPersonalName == true) {  // Words is PersonalName
                        if (isExpressionPersonalName == true) {
                            isPersonalName = isPartPersonalName;
                            countPersonalName++;
                            partsPersonalName += part + " ";
                            System.out.println("5.1. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                        }
                    } else {  // Words isn't PersonalName
                        if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                            isPersonalName = false;
                            result += "Физическо лице" + " ";
                            System.out.println("5.2. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                            partsPersonalName = "";
                        } else {
                            isTextEgn = checkIsTextEgn(part);
                            if (isTextEgn == true) {  // Word is Egn!
                                result += "";
                                System.out.println("5.3. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                            } else {
                                result += part + " ";
                                System.out.println("5.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                            }  // Is Egn?
                            partsPersonalName = "";
                        }
                    }  // Is PersonalName?
                } else {  // Word Not Only String
                    if (countPersonalName > 0) {  // If we already have one personal name!
                        isPartPersonalName = checkIsPartPersonalName(part, countPersonalName);
                        if (isPartPersonalName == true) {  // Words is PersonalName
                            if (isExpressionPersonalName == true) {
                                isPersonalName = isPartPersonalName;
                                countPersonalName++;
                                partsPersonalName += part + " ";
                                System.out.println("5.1. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                            }
                        } else {  // Words isn't PersonalName
                            if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                                isPersonalName = false;
                                result += "Физическо лице" + " ";
                                partsPersonalName = "";
                                System.out.println("7.1. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | part: " + part + " | result: " + result + "");
                            } else {  // If the previous words aren't PersonalName
                                isPartOnlyDigits = checkIsPartOnlyDigits(part);
                                if (isPartOnlyDigits == true) {  // Word is Only Digits
                                    if (isHasEgn == true && isHasBulstat == false) {  // If the expression contains word EGN AND does not contain word Bulstat!
                                        isPartValidEgn = checkIsPartValidEgn(part);
                                        if (isPartValidEgn == true) {  // Word is valid EGN!
                                            encryptEgn = encryptionEgn(part);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += "ЕГН " + hashEncryptEgn + " ";
                                            System.out.println("7.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                        } else {  // Word isn't valid EGN!
                                            if (part.length() == 10) {
                                                encryptEgn = encryptionEgn(part);
                                                hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                                result += " " + hashEncryptEgn + " ";
                                                System.out.println("7.3. isPartOnlyDigits: " + isPartOnlyDigits + " | IF (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                            } else {
                                                result += part + " ";
                                                System.out.println("7.4. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                            }
                                        }  // Is valid EGN?
                                    } else {  // Word isn't valid EGN! Is the word is Bulstat - not change it!
                                        isPartValidEgn = checkIsPartValidEgn(part);
                                        if (isPartValidEgn == true) {  // Word is valid EGN!
                                            encryptEgn = encryptionEgn(part);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += "ЕГН " + hashEncryptEgn + " ";
                                            System.out.println("7.5. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                        } else {  // Word isn't valid EGN!
                                            result += part + " ";
                                            System.out.println("7.6. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                        }  // Is valid EGN?
                                    }  // Contains the EGN/Bulstat?
                                } else {  // Word does NOT Only Digits
                                    if (isHasBulstat == true) {  // If the expression contains word Bulstat!
                                        isTextBulstat = checkIsTextBulstat(part);
                                        if (isTextBulstat == true) {  // Word is Bulstat!
                                            result += "Булстат" + " ";
                                            System.out.println("8.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                        } else {  // Word isn't Bulstat!
                                            result += part + " ";
                                            System.out.println("8.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                        }  // Is word Bulstat?
                                    } else {  // If the expression doesn't contains word Bulstat!
                                        isPartCombineEgn = checkIsPartCombineEgn(part);
                                        if (isPartCombineEgn == true) {  // The word is a combination of EGN as 'ЕГН0123456789'!
                                            resultPart = getPartDigit(part);
                                            encryptEgn = encryptionEgn(resultPart);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += "ЕГН " + hashEncryptEgn + " ";
                                            System.out.println("8.3. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartCombineEgn " + isPartCombineEgn + " | part: " + part + " | result: " + result + "");
                                        } else {  // The word isn't combination of EGN!
                                            isTextEgn = checkIsTextEgn(part);
                                            if (isTextEgn == true) {  // Word is Egn!
                                                result += "";
                                                System.out.println("8.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                            } else {
                                                isPartPersonalName = checkIsPartPersonalName(part);
                                                if (isPartPersonalName == true) {  // Words is PersonalName
                                                    if (isExpressionPersonalName == true) {
                                                        isPersonalName = isPartPersonalName;
                                                        countPersonalName++;
                                                        partsPersonalName += part + " ";
                                                        System.out.println("8.5. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                    }
                                                } else {  // Words isn't PersonalName
                                                    if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                                                        isPersonalName = false;
                                                        result += "Физическо лице" + " ";
                                                        System.out.println("8.6. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                        partsPersonalName = "";
                                                    } else {
                                                        result += part + " ";
                                                        System.out.println("8.7. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                                    }
                                                }  // Is PersonalName?
                                            }  // Is Egn?
                                        }  // Is word  a combination of EGN?
                                    }  // Is contains word Bulstat?
                                }  // Is Only Digits?
                            }  // Is previous words are PersonalName?
                        }  // Is PersonalName?
                    } else {  // We don't have a personal name!
                        if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                            isPersonalName = false;
                            result += "Физическо лице" + " ";
                            partsPersonalName = "";
                            System.out.println("9.1. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | part: " + part + " | result: " + result + "");
                        } else {  // If the previous words aren't PersonalName
                            isPartOnlyDigits = checkIsPartOnlyDigits(part);
                            if (isPartOnlyDigits == true) {  // Word is Only Digits
                                if (isHasEgn == true && isHasBulstat == false) {  // If the expression contains word EGN AND does not contain word Bulstat!
                                    isPartValidEgn = checkIsPartValidEgn(part);
                                    if (isPartValidEgn == true) {  // Word is valid EGN!
                                        encryptEgn = encryptionEgn(part);
                                        hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                        result += "ЕГН " + hashEncryptEgn + " ";
                                        System.out.println("9.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't valid EGN!
                                        if (part.length() == 10) {
                                            encryptEgn = encryptionEgn(part);
                                            hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                            result += " " + hashEncryptEgn + " ";
                                            System.out.println("9.3. isPartOnlyDigits: " + isPartOnlyDigits + " | IF (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                        } else {
                                            result += part + " ";
                                            System.out.println("9.4. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                        }
                                    }  // Is valid EGN?
                                } else {  // Word isn't valid EGN! Is the word is Bulstat - not change it!
                                    isPartValidEgn = checkIsPartValidEgn(part);
                                    if (isPartValidEgn == true) {  // Word is valid EGN!
                                        encryptEgn = encryptionEgn(part);
                                        hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                        result += "ЕГН " + hashEncryptEgn + " ";
                                        System.out.println("9.5. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartValidEgn: " + isPartValidEgn + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't valid EGN!
                                        result += part + " ";
                                        System.out.println("9.6. isPartOnlyDigits: " + isPartOnlyDigits + " | ELSE (part.length() == 10) " + " | part: " + part + " | result: " + result + "");
                                    }  // Is valid EGN?
                                }  // Contains the EGN/Bulstat?
                            } else {  // Word does NOT Only Digits
                                if (isHasBulstat == true) {  // If the expression contains word Bulstat!
                                    isTextBulstat = checkIsTextBulstat(part);
                                    if (isTextBulstat == true) {  // Word is Bulstat!
                                        result += "Булстат" + " ";
                                        System.out.println("10.1. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                    } else {  // Word isn't Bulstat!
                                        result += part + " ";
                                        System.out.println("10.2. isPartOnlyDigits: " + isPartOnlyDigits + " | isTextBulstat: " + isTextBulstat + " | part: " + part + " | result: " + result + "");
                                    }  // Is word Bulstat?
                                } else {  // If the expression doesn't contains word Bulstat!
                                    isPartCombineEgn = checkIsPartCombineEgn(part);
                                    if (isPartCombineEgn == true) {  // The word is a combination of EGN as 'ЕГН0123456789'!
                                        resultPart = getPartDigit(part);
                                        encryptEgn = encryptionEgn(resultPart);
                                        hashEncryptEgn = hashEncryptionEgn(encryptEgn);
                                        result += "ЕГН " + hashEncryptEgn + " ";
                                        System.out.println("10.3. isPartOnlyDigits: " + isPartOnlyDigits + " | isPartCombineEgn " + isPartCombineEgn + " | part: " + part + " | result: " + result + "");
                                    } else {  // The word isn't combination of EGN!
                                        isTextEgn = checkIsTextEgn(part);
                                        if (isTextEgn == true) {  // Word is Egn!
                                            result += "";
                                            System.out.println("10.4. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                        } else {
                                            isPartPersonalName = checkIsPartPersonalName(part);
                                            if (isPartPersonalName == true) {  // Words is PersonalName
                                                if (isExpressionPersonalName == true) {
                                                    isPersonalName = isPartPersonalName;
                                                    countPersonalName++;
                                                    partsPersonalName += part + " ";
                                                    System.out.println("10.5. isPartOnlyString: " + isPartOnlyString + " | isPartPersonalName: " + isPartPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                }
                                            } else {  // Words isn't PersonalName
                                                if (isPersonalName == true && (countPersonalName >= 2 && countPersonalName < 4)) {  // If the previous words are PersonalName
                                                    isPersonalName = false;
                                                    result += "Физическо лице" + " ";
                                                    System.out.println("10.6. isPartOnlyString: " + isPartOnlyString + " | isPersonalName: " + isPersonalName + " | partsPersonalName: " + partsPersonalName + " | part: " + part + " | result: " + result + "");
                                                    partsPersonalName = "";
                                                } else {
                                                    result += part + " ";
                                                    System.out.println("10.7. isPartOnlyString: " + isPartOnlyString + " | isTextEgn: " + isTextEgn + " | part: " + part + " | result: " + result + "");
                                                }
                                            }  // Is PersonalName?
                                        }  // Is Egn?
                                    }  // Is word  a combination of EGN?
                                }  // Is contains word Bulstat?
                            }  // Is Only Digits?
                        }  // Is previous words are PersonalName?
                    }  // Have we personal name?
                }  // Is word Only String?
            }  // Is expression contains non personal indicators?
        }  // for

        lengthPartsPersonalName = partsPersonalName.length();
        lengthResult = result.length();
        if (lengthPartsPersonalName > 0) {
            if (lengthResult > 0) {
                if (countPersonalName >= 2) {
                    result = "Физическо лице" + " ";
                    System.out.println("11.1. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                } else {
                    result += partsPersonalName.trim() + " ";
                    System.out.println("11.2. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                }
            } else {
                if (countPersonalName >= 2) {
                    result = "Физическо лице" + " ";
                    System.out.println("11.3. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                } else {
                    result += partsPersonalName.trim() + " ";
                    System.out.println("11.4. lengthPartsPersonalName: " + lengthPartsPersonalName + " | lengthResult: " + lengthResult + " | result: " + result + "");
                }
            }
            partsPersonalName = "";
        }

        result = result.trim().toUpperCase();
        System.out.println("12. result: " + result + "");
        return result;
    }

    private String anonymizeReceiversIban(String iban, String beneficiary) {
        String encryptIban = "";
        String hashEncryptIban = "";
        String result = "";
        Boolean isHasPerson = false;

        if ((beneficiary.matches("\\bФИЗИЧЕСКО ЛИЦЕ\\b"))
                || (beneficiary.matches("\\bФИЗИЧЕСКО ЛИЦЕ\\b.+"))
                || (beneficiary.matches("\\bФИЗИЧЕСКО ЛИЦЕ\\s\\b.+"))
                || (beneficiary.matches(".+\\bФИЗИЧЕСКО ЛИЦЕ\\b"))
                || (beneficiary.matches(".+\\bФИЗИЧЕСКО ЛИЦЕ\\b.+"))
                || (beneficiary.matches(".+\\bФИЗИЧЕСКО ЛИЦЕ\\s\\b.+"))
                || (beneficiary.matches("ФИЗИЧЕСКО ЛИЦЕ\\b"))
                || (beneficiary.matches("ФИЗИЧЕСКО ЛИЦЕ\\b.+"))
                || (beneficiary.matches("ФИЗИЧЕСКО ЛИЦЕ\\s\\b.+"))
                || (beneficiary.matches("ФИЗИЧЕСКО ЛИЦЕ\\s"))
                || (beneficiary.matches("ФИЗИЧЕСКО ЛИЦЕ"))) {  // With/Without a word Before/After the isHasPerson, With/Without a point, With/Without a space after the word isHasPerson!
            isHasPerson = true;
            encryptIban = encryptionIban(iban);
            hashEncryptIban = hashEncryptionIban(encryptIban);
            iban = hashEncryptIban;
        } else {  // The expression does NOT contain an isHasPerson!
            isHasPerson = false;
        }

        result = iban;
        System.out.println("20. Result iban: " + result + "");
        return result;
    }

    private static String untransliterate(String reason) {
        String result = TRANSLITERATOR.transliterate(reason);
        return result;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getPathInCsv() {
        return inCsv;
    }

    public void setPathInCsv(String inCsv) {
        this.inCsv = inCsv;
    }

    public String getPathOrgCsv() {
        return orgCsv;
    }

    public void setPathOrgCsv(String orgCsv) {
        this.orgCsv = orgCsv;
    }

    public String getPathSaltTxt() {
        return saltTxt;
    }

    public void setPathSaltTxt(String saltTxt) {
        this.saltTxt = saltTxt;
    }

    public String getPathOutCsv() {
        return outCsv;
    }

    public void setPathOutCsv(String outCsv) {
        this.outCsv = outCsv;
    }

    public String getPathFolder() {
        return folder;
    }

    public void setPathFolder(String folder) {
        this.folder = folder;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    // ////////////////////////////////////////
    public String getPathInDelRegNumCsv() {
        return inDelRegNumCsv;
    }

    public void setPathInDelRegNumCsv(String inDelRegNumCsv) {
        this.inDelRegNumCsv = inDelRegNumCsv;
    }

    public String getPathOutDelRegNumCsv() {
        return outDelRegNumCsv;
    }

    public void setPathOutDelRegNumCsv(String outDelRegNumCsv) {
        this.outDelRegNumCsv = outDelRegNumCsv;
    }

    public String getPathRegNumCsv() {
        return regNumCsv;
    }

    public void setPathRegNumCsv(String regNumCsv) {
        this.regNumCsv = regNumCsv;
    }

    public String getPathRejectedDelRegNumCsv() {
        return rejectedDelRegNumCsv;
    }

    public void setPathRejectedDelRegNumCsv(String rejectedDelRegNumCsv) {
        this.rejectedDelRegNumCsv = rejectedDelRegNumCsv;
    }

    public String getPathAutodelRegNumCsv() {
        return autodelRegNumCsv;
    }

    public void setPathAutodelRegNumCsv(String autodelRegNumCsv) {
        this.autodelRegNumCsv = autodelRegNumCsv;
    }

    public String getPathInDelSebraCodesCsv() {
        return inDelSebraCodesCsv;
    }

    public void setPathInDelSebraCodesCsv(String inDelSebraCodesCsv) {
        this.inDelSebraCodesCsv = inDelSebraCodesCsv;
    }

    public String getPathOutDelSebraCodesCsv() {
        return outDelSebraCodesCsv;
    }

    public void setPathOutDelSebraCodesCsv(String outDelSebraCodesCsv) {
        this.outDelSebraCodesCsv = outDelSebraCodesCsv;
    }

    public String getPathSebraCodesCsv() {
        return sebraCodesCsv;
    }

    public void setPathSebraCodesCsv(String sebraCodesCsv) {
        this.sebraCodesCsv = sebraCodesCsv;
    }

    public String getPathAutodelSebraCodesCsv() {
        return autodelDelSebraCodesCsv;
    }

    public void setPathAutodelSebraCodesCsv(String autodelDelSebraCodesCsv) {
        this.autodelDelSebraCodesCsv = autodelDelSebraCodesCsv;
    }

    public void makeDeletionByRegistrationNumber() {
        inDelRegNumCsv = getPathInDelRegNumCsv();  // Файл за изтриване по номер на регистрация! | File to delete by registration number!
        regNumCsv = getPathRegNumCsv();  // Файл-масив с номера на регистрация и себра кодове! | File-array with registration numbers and sebra codes!
        outDelRegNumCsv = getPathOutDelRegNumCsv();  // Файл с резултатни данни от изтриването по номер на регистрация! | Delete result data file by registration number!
        rejectedDelRegNumCsv = getPathRejectedDelRegNumCsv();  // Файл с отхвърлени (не изтрити автоматично) записи при изтриването по номер на регистрация! | File with rejected (not automatically deleted) records when deleting by registration number!
        autodelRegNumCsv = getPathAutodelRegNumCsv();  // Файл с автоматично изтрити записи при изтриването по номер на регистрация! | File with automatically deleted records when deleting by registration number!
        String msg = null;
        String sebra_name = "";  // RegistrationNumbers.sebraName
        String sebra_code = "";  // RegistrationNumbers.sebraCode
        String reg_number = "";  // RegistrationNumbers.regNumber
        String fin_name = "";  // inDelRegNumCsv.FIN_NAME
        String fin_code = "";  // inDelRegNumCsv.FIN_CODE
        String reg_no = "";  // inDelRegNumCsv.REG_NO
        Boolean isExistRegNum = false;
        int autodelRecords = 0;
        int rejectedRecords = 0;
        ArrayList<RegistrationNumbers> listRegNum = new ArrayList<RegistrationNumbers>();
        ArrayList<RegistrationNumbers> listAutodel = new ArrayList<RegistrationNumbers>();
        File fileAutodel = new File(autodelRegNumCsv);
        File fileRejected = new File(rejectedDelRegNumCsv);
        List<String[]> autodelResult = new ArrayList<String[]>();
        List<String[]> rejectedResult = new ArrayList<String[]>();

        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(regNumCsv)), "utf-8")) {
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                RegistrationNumbers registrationNumbers = new RegistrationNumbers();
                sebra_name = record.get(0);
                sebra_code = record.get(1);
                reg_number = record.get(2);
                registrationNumbers.sebraName = sebra_name.trim();  // sebra_name.trim().toUpperCase();  // sebra_name.toLowerCase().trim();
                registrationNumbers.sebraCode = sebra_code.trim();  // sebra_code.trim().toUpperCase();  // sebra_code.toLowerCase().trim();
                registrationNumbers.regNumber = reg_number.trim();  // reg_number.trim().toUpperCase();  // reg_number.toLowerCase().trim();
                listRegNum.add(registrationNumbers);
            }
            reader.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(inDelRegNumCsv)), "utf-8"); OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outDelRegNumCsv)), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                autodelRecords = 0;
                FileWriter outputAutodel = new FileWriter(fileAutodel);
                CSVWriter writerAutodel = new CSVWriter(outputAutodel, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
                autodelResult.add(new String[]{"FIN_NAME", "FIN_CODE", "REG_NO"});

                for (CSVRecord record : parser) {
                    try {
                        List<String> rowIn = new ArrayList<>();
                        for (int i = 0; i < record.size(); i++) {
                            rowIn.add(record.get(i).trim());
                        }
                        fin_code = record.get(4).toLowerCase().trim();  // FIN_CODE // fin_code.trim().toUpperCase();
                        fin_name = record.get(5).toLowerCase().trim();  // FIN_NAME // fin_name.trim().toUpperCase();
                        reg_no = record.get(11).toLowerCase().trim();  // REG_NO // reg_no.trim().toUpperCase();
                        isExistRegNum = false;
                        for (RegistrationNumbers listRN : listRegNum) {
                            try {
                                sebra_name = listRN.sebraName.toLowerCase();
                                sebra_code = listRN.sebraCode.toLowerCase();
                                reg_number = listRN.regNumber.toLowerCase();
                                if (fin_code.equalsIgnoreCase(sebra_code) && reg_no.equalsIgnoreCase(reg_number)) {
                                    isExistRegNum = true;  // Записът е намерен и изтрит! | Record found and deleted!
                                    autodelRecords++;
                                    autodelResult.add(new String[]{sebra_name.toUpperCase(), sebra_code.toUpperCase(), reg_number.toUpperCase()});
                                    break;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (isExistRegNum == false) {
                            printer.printRecord(rowIn);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                writerAutodel.writeAll(autodelResult);
                printer.flush();
                writerAutodel.close();
                writer.close();
            }

            reader.close();
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (Reader readerAutodel = new InputStreamReader(new BufferedInputStream(new FileInputStream(autodelRegNumCsv)), "utf-8")) {
            CSVParser parserAutodel = CSVParser.parse(readerAutodel, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parserAutodel) {
                RegistrationNumbers registrationNumbers = new RegistrationNumbers();
                sebra_name = record.get(0);
                sebra_code = record.get(1);
                reg_number = record.get(2);
                registrationNumbers.sebraName = sebra_name.trim();  // sebra_name.trim().toUpperCase();  // sebra_name.toLowerCase().trim();
                registrationNumbers.sebraCode = sebra_code.trim();  // sebra_code.trim().toUpperCase();  // sebra_code.toLowerCase().trim();
                registrationNumbers.regNumber = reg_number.trim();  // reg_number.trim().toUpperCase();  // reg_number.toLowerCase().trim();
                listAutodel.add(registrationNumbers);
            }
            readerAutodel.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            rejectedRecords = 0;
            FileWriter outputRejected = new FileWriter(fileRejected);
            CSVWriter writerRejected = new CSVWriter(outputRejected, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            rejectedResult.add(new String[]{"FIN_NAME", "FIN_CODE", "REG_NO"});

            for (RegistrationNumbers listRN : listRegNum) {
                fin_name = listRN.sebraName.toLowerCase();
                fin_code = listRN.sebraCode.toLowerCase();
                reg_no = listRN.regNumber.toLowerCase();
                isExistRegNum = false;
                for (RegistrationNumbers listAD : listAutodel) {
                    sebra_name = listAD.sebraName.toLowerCase();
                    sebra_code = listAD.sebraCode.toLowerCase();
                    reg_number = listAD.regNumber.toLowerCase();
                    if (fin_code.equalsIgnoreCase(sebra_code) && reg_no.equalsIgnoreCase(reg_number)) {
                        isExistRegNum = true;  // Записът е намерен в автоматично изтритите редове! | Record found in auto-deleted rows!
                        break;
                    }
                }
                if (isExistRegNum == false) {
                    rejectedRecords++;
                    rejectedResult.add(new String[]{fin_name.toUpperCase(), fin_code.toUpperCase(), reg_no.toUpperCase()});
                }
            }
            writerRejected.writeAll(rejectedResult);
            writerRejected.close();

            taText = " Изтриването завърши успешно!";
            setDataGeneralStatisticsTextArea(taText);
            taText = " • Изтрити редове: " + String.valueOf(autodelRecords);
            setDataGeneralStatisticsTextArea(taText);
            taText = " • Отхвърлени редове: " + String.valueOf(rejectedRecords);
            setDataGeneralStatisticsTextArea(taText);
            taText = "------------------------------------------------------------------------------------------------------------------";
            setDataGeneralStatisticsTextArea(taText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void makeDeletionBySebraCode() {
        inDelSebraCodesCsv = getPathInDelSebraCodesCsv();  // Файл за изтриване по себра код! | File to delete by sebra code!
        sebraCodesCsv = getPathSebraCodesCsv();  // Файл-масив със себра кодове! | File-array with sebra codes!
        outDelSebraCodesCsv = getPathOutDelSebraCodesCsv();  // Файл с резултатни данни от изтриването по себра код! | A file with the result data of the deletion by sebra code!
        autodelDelSebraCodesCsv = getPathAutodelSebraCodesCsv();  // Файл с изтрити записи при изтриване по себра код! | Deleted records file when deleting by sebra code!
        String msg = null;
        String sebra_code = "";  // SebraCodes.sebraCode
        String fin_code = "";  // inDelSebraCodesCsv.FIN_CODE
        Boolean isExistSebraCode = false;
        int autodelRecords = 0;
        ArrayList<SebraCodes> listSebraCodes = new ArrayList<SebraCodes>();

        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(sebraCodesCsv)), "utf-8")) {
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                SebraCodes sebraCodes = new SebraCodes();
                sebra_code = record.get(0);
                sebraCodes.sebraCode = sebra_code.toLowerCase().trim();  // sebra_code.trim().toUpperCase();
                listSebraCodes.add(sebraCodes);
            }
            reader.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(inDelSebraCodesCsv)), "utf-8"); OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outDelSebraCodesCsv)), StandardCharsets.UTF_8)) {
            OutputStreamWriter writerAutodel = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(autodelDelSebraCodesCsv)), StandardCharsets.UTF_8);
            CSVPrinter printerAutodel = new CSVPrinter(writerAutodel, CSVFormat.DEFAULT);
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                autodelRecords = 0;
                for (CSVRecord record : parser) {
                    try {
                        List<String> row = new ArrayList<>();
                        for (int i = 0; i < record.size(); i++) {
                            row.add(record.get(i).trim());
                        }
                        fin_code = record.get(4).toLowerCase().trim();  // FIN_CODE // fin_code.trim().toUpperCase();
                        isExistSebraCode = false;
                        for (SebraCodes listSC : listSebraCodes) {
                            sebra_code = listSC.sebraCode;
                            if (fin_code.equalsIgnoreCase(sebra_code)) {
                                isExistSebraCode = true;
                                autodelRecords++;
                                break;
                            }
                        }
                        if (isExistSebraCode == false) {
                            printer.printRecord(row);
                        } else {
                            printerAutodel.printRecord(row);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                printer.flush();
                printerAutodel.flush();
            }
            reader.close();
            writer.close();
            writerAutodel.close();

            taText = " Изтриването завърши успешно!";
            setDataGeneralStatisticsTextArea(taText);
            taText = " • Изтрити редове: " + String.valueOf(autodelRecords);
            setDataGeneralStatisticsTextArea(taText);
            taText = "------------------------------------------------------------------------------------------------------------------";
            setDataGeneralStatisticsTextArea(taText);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class RegistrationNumbers {
        public String sebraName;
        public String sebraCode;
        public String regNumber;
    }

    public class SebraCodes {
        public String sebraCode;
    }
    // //////////////////////////

    public static class Organization {
        private String code;
        private String name;
        private String description;
        private LocalDateTime from;
        private LocalDateTime to;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getFrom() {
            return from;
        }

        public void setFrom(LocalDateTime from) {
            this.from = from;
        }

        public LocalDateTime getTo() {
            return to;
        }

        public void setTo(LocalDateTime to) {
            this.to = to;
        }

    }

    public Boolean checkIsHasAddress(String lowerCaseString) {
        Boolean isHasAddress = false;

        if ((lowerCaseString.matches("\\bбул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("ул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("ул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("ул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("ул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+ул\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+ул\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+ул\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+ул\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("ул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("ул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+ул\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+ул\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбл\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбл\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбл\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбл\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбл\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбл\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бл\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бл\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бл\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бл\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бл\\.[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бл\\.\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бл\\.[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бл\\.\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбл\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("\\bбл\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+\\bбл\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("бл\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches("бл\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches(".+бл\\s[а-яА-Я]+\\b"))
                || (lowerCaseString.matches(".+бл\\s[а-яА-Я]+.+"))
                || (lowerCaseString.matches("\\bбл\\.\\d+\\b"))
                || (lowerCaseString.matches("\\bбл\\.\\s\\d+\\b"))
                || (lowerCaseString.matches("\\bбл\\.\\d+.+"))
                || (lowerCaseString.matches("\\bбл\\.\\s\\d+.+"))
                || (lowerCaseString.matches(".+\\bбл\\.\\d+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\.\\s\\d+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\.\\d+.+"))
                || (lowerCaseString.matches(".+\\bбл\\.\\s\\d+.+"))
                || (lowerCaseString.matches("бл\\.\\d+\\b"))
                || (lowerCaseString.matches("бл\\.\\s\\d+\\b"))
                || (lowerCaseString.matches("бл\\.\\d+.+"))
                || (lowerCaseString.matches("бл\\.\\s\\d+.+"))
                || (lowerCaseString.matches(".+бл\\.\\d+\\b"))
                || (lowerCaseString.matches(".+бл\\.\\s\\d+\\b"))
                || (lowerCaseString.matches(".+бл\\.\\d+.+"))
                || (lowerCaseString.matches(".+бл\\.\\s\\d+.+"))
                || (lowerCaseString.matches("\\bбл\\d+\\b"))
                || (lowerCaseString.matches("\\bбл\\s\\d+\\b"))
                || (lowerCaseString.matches("\\bбл\\d+.+"))
                || (lowerCaseString.matches("\\bбл\\s\\d+.+"))
                || (lowerCaseString.matches(".+\\bбл\\d+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\s\\d+\\b"))
                || (lowerCaseString.matches(".+\\bбл\\d+.+"))
                || (lowerCaseString.matches(".+\\bбл\\s\\d+.+"))
                || (lowerCaseString.matches("бл\\d+\\b"))
                || (lowerCaseString.matches("бл\\s\\d+\\b"))
                || (lowerCaseString.matches("бл\\d+.+"))
                || (lowerCaseString.matches("бл\\s\\d+.+"))
                || (lowerCaseString.matches(".+бл\\d+\\b"))
                || (lowerCaseString.matches(".+бл\\s\\d+\\b"))
                || (lowerCaseString.matches(".+бл\\d+.+"))
                || (lowerCaseString.matches(".+бл\\s\\d+.+"))) {
            isHasAddress = true;
        } else {
            isHasAddress = false;
        }

        return isHasAddress;
    }

    public Boolean checkIsHasBulstat(String lowerCaseString) {
        Boolean isHasBulstat = false;
        Boolean isPartBulstat = false;
        Boolean isAllPartBulstat = false;
        Boolean isPartOnlyDigits = false;
        Boolean isPartCombineBulstat = false;

        String[] parts = lowerCaseString.split("\\s+");
        for (String part : parts) {
            if ((part.matches("\\bбул\\.\\d+\\b"))
                    || (part.matches("\\bбул\\d+\\b"))
                    || (part.matches("\\bбул\\.\\b"))
                    || (part.matches("\\bбул\\b"))
                    || (part.matches("бул\\.\\d+\\b"))
                    || (part.matches("бул\\d+\\b"))
                    || (part.matches("бул\\.\\b"))
                    || (part.matches("бул\\b"))
                    || (part.matches("бул\\."))
                    || (part.matches("бул"))) {
                isPartBulstat = true;
            } else {
                isPartBulstat = false;
            }
            isAllPartBulstat = (isAllPartBulstat || isPartBulstat);
        }

        if (isAllPartBulstat == true) {  // In expression has Bulevard/Bulstat!
            isPartBulstat = false;
            isAllPartBulstat = false;
            for (String part : parts) {
                isPartOnlyDigits = checkIsPartOnlyDigits(part);
                if (isPartOnlyDigits == true) {  // Word is Only Digits
                    if (part.length() == 9 || part.length() == 13) {  // Part is Bulstat!
                        isPartBulstat = true;
                    }  // Is part Bulstat?
                } else {
                    isPartCombineBulstat = checkIsPartCombineBulstat(part);
                    if (isPartCombineBulstat == true) {  // The word is a combination of EGN as 'Бул.0123456789'!
                        isPartBulstat = true;
                    } else {  // The word isn't combination of EGN!
                        isPartBulstat = false;
                    }  // Is word  a combination of EGN?
                }
                isAllPartBulstat = (isAllPartBulstat || isPartBulstat);
            }
            isHasBulstat = isAllPartBulstat;
        } else {  // In expression hasn't Bulstat!
            isPartBulstat = false;
            isAllPartBulstat = false;
            for (String part : parts) {
                isPartOnlyDigits = checkIsPartOnlyDigits(part);
                if (isPartOnlyDigits == true) {  // Word is Only Digits
                    if (part.length() == 9 || part.length() == 13) {  // Part is Bulstat!
                        isPartBulstat = true;
                    }  // Is part Bulstat?
                } else {
                    isPartCombineBulstat = checkIsPartCombineBulstat(part);
                    if (isPartCombineBulstat == true) {  // The word is a combination of EGN as 'Бул.0123456789'!
                        isPartBulstat = true;
                    } else {  // The word isn't combination of EGN!
                        isPartBulstat = false;
                    }  // Is word  a combination of EGN?
                }
                isAllPartBulstat = (isAllPartBulstat || isPartBulstat);
            }
            isHasBulstat = isAllPartBulstat;
        }  // Is in expression has Bulstat?

        return isHasBulstat;
    }

    public Boolean checkIsHasEgn(String lowerCaseString) {
        Boolean isHasEgn = false;

        if ((lowerCaseString.matches("\\bегн\\b"))
                || (lowerCaseString.matches("\\bегн\\b.+"))
                || (lowerCaseString.matches("\\bегн\\s\\b.+"))
                || (lowerCaseString.matches(".+\\bегн\\b"))
                || (lowerCaseString.matches(".+\\bегн\\b.+"))
                || (lowerCaseString.matches(".+\\bегн\\s\\b.+"))
                || (lowerCaseString.matches("егн\\b"))
                || (lowerCaseString.matches("егн\\b.+"))
                || (lowerCaseString.matches("егн\\s\\b.+"))
                || (lowerCaseString.matches("егн.+"))
                || (lowerCaseString.matches("егн\\s"))
                || (lowerCaseString.matches("егн"))) {  // With/Without a word Before/After the EGN, With/Without a point, With/Without a space after the word EGN!
            isHasEgn = true;
        } else {  // The expression does NOT contain an EGN!
            isHasEgn = false;
        }

        return isHasEgn;
    }

    public Boolean checkIsExpressionPersonalName(String lowerCaseString) {
        Boolean isExpressionPersonalName = false;
        String[] parts = lowerCaseString.split("\\s+");

        for (String part : parts) {
            for (String ns : Config.NAME_SUFFIXES) {
                if ((part.matches(".+\\p{L}{2,}" + ns + "")
                        || part.matches(".+\\p{L}{2,}" + ns + "\\b.+")
                        || part.matches("\\p{L}{2,}" + ns + "")
                        || part.matches("\\p{L}{2,}" + ns + "\\b.+"))
                        && ((!part.contains("\""))
                        && (!part.contains("."))
                        && (!part.contains(","))
                        && (!part.contains("&"))
                        && (!part.contains("'")))) {
                    isExpressionPersonalName = true;
                    break;
                }
            }
            if (isExpressionPersonalName == true) {
                break;
            }
        }
        if (isExpressionPersonalName == false) {
            for (String part : parts) {
                for (String opn : Config.OTHER_PERSONAL_NAMES) {
                    if (part.matches(opn)
                            || part.matches("\\b" + opn + "\\b")
                            || part.matches("[-]" + opn + "\\b")
                            || part.matches(".+\\," + opn + "\\b")
                            || part.matches(".+\\." + opn + "\\b")
                            || part.matches(".+\\." + opn + "")
                            || part.matches("\\b" + opn + "[-].+")
                            || part.matches("\\b" + opn + "\\.\\b")
                            || part.matches("\\b" + opn + "\\..+")
                            || part.matches("\\b" + opn + "\\,.+")
                            || part.matches(opn + "[-].+")
                            || part.matches(opn + "\\.\\b")
                            || part.matches(opn + "\\,\\b")
                            || part.matches(opn + "\\..+")
                            || part.matches(opn + "\\,.+")
                            || part.matches(opn)) {
                        isExpressionPersonalName = true;
                        break;
                    }
                }
                if (isExpressionPersonalName == true) {
                    break;
                }
            }
        }

        return isExpressionPersonalName;
    }

    public Boolean checkIsExpressionNonPersonalIndicators(String lowerCaseString) {
        Boolean isExpressionNonPersonalIndicators = false;

        String[] parts = lowerCaseString.split("\\s+");
        for (String part : parts) {
            for (String npi : Config.NON_PERSONAL_INDICATORS) {
                if (part.matches(npi)
                        || part.matches("\\b" + npi + "\\b")
                        || part.matches("[-]" + npi + "\\b")
                        || part.matches(".+[-]" + npi + "\\b")
                        || part.matches(".+\\," + npi + "\\b")
                        || part.matches(".+\\." + npi + "\\b")
                        || part.matches("\\b" + npi + "[-].+")
                        || part.matches("\\b" + npi + "\\.\\b")
                        || part.matches("\\b" + npi + "\\.\\d+")
                        || part.matches("\\b" + npi + "\\,\\d+")
                        || part.matches("\\b" + npi + "\\..+")
                        || part.matches("\\b" + npi + "\\,.+")
                        || part.matches(npi + "[-].+")
                        || part.matches(npi + "\\.\\b")
                        || part.matches(npi + "\\.\\d+")
                        || part.matches(npi + "\\,\\d+")
                        || part.matches(npi + "\\..+")
                        || part.matches(npi + "\\,.+")
                        || part.matches(npi)) {
                    isExpressionNonPersonalIndicators = true;
                    break;
                }
            }
            if (isExpressionNonPersonalIndicators == true) {
                break;
            }
        }

        return isExpressionNonPersonalIndicators;
    }

    public Boolean checkIsPartOnlyString(String part) {
        Boolean isPartOnlyString = false;

        if ((part.matches(".+\\b^[а-яА-Я]+\\b"))
                || (part.matches("\\b^[а-яА-Я]+\\b"))) {  // Word Only String
            isPartOnlyString = true;
        } else {  // Word NOT Only String
            isPartOnlyString = false;
        }

        return isPartOnlyString;
    }

    public Boolean checkIsPartOnlyDigits(String part) {
        Boolean isPartOnlyDigits = false;

        if ((part.matches("\\b\\d+\\b"))
                || (part.matches("\\d+\\b"))
                || (part.matches("\\d+"))) {  // Word Only Digits
            isPartOnlyDigits = true;
        } else {  // Word NOT Only Digits
            isPartOnlyDigits = false;
        }

        return isPartOnlyDigits;
    }

    public Boolean checkIsPartPersonalName(String part) {
        Boolean isPartPersonalName = false;

        if (((Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches(".+\\p{L}{2,}" + s + "")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches(".+\\p{L}{2,}" + s + "\\b.+")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches("\\p{L}{2,}" + s + "")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches("\\p{L}{2,}" + s + "\\b.+")))
                || (Config.OTHER_PERSONAL_NAMES.stream().anyMatch(name -> part.contains(name))))
                && ((!part.contains("\""))
                && (!part.contains("."))
                && (!part.contains(","))
                && (!part.contains("&"))
                && (!part.contains("'")))) {  // Yes, there are Suffix in the word!  // Yes, there are Name in the word!
            isPartPersonalName = true;
        } else {  // No, there aren't Suffix in the word!  // No, there aren't Name in the word!
            isPartPersonalName = false;
        }

        return isPartPersonalName;
    }

    public Boolean checkIsPartPersonalName(String part, int countPersonalName) {
        Boolean isPartPersonalName = false;

        if (((Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches(".+\\p{L}{2,}" + s + "")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches(".+\\p{L}{2,}" + s + "\\b.+")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches("\\p{L}{2,}" + s + "")))
                || (Config.NAME_SUFFIXES.stream().anyMatch(s -> part.matches("\\p{L}{2,}" + s + "\\b.+")))
                || (Config.OTHER_PERSONAL_NAMES.stream().anyMatch(name -> part.contains(name))))
                && ((!part.contains("\""))
                && (!part.contains(","))
                && (!part.contains("&"))
                && (!part.contains("'")))) {  // Yes, there are Suffix in the word!  // Yes, there are Name in the word!
            isPartPersonalName = true;
        } else {  // No, there aren't Suffix in the word!  // No, there aren't Name in the word!
            isPartPersonalName = false;
        }

        return isPartPersonalName;
    }

    public Boolean checkIsPartValidEgn(String part) {
        Boolean isPartValidEgn = false;
        int ctrSum = 0;
        int ctrField = 0;
        int teglo[] = {2, 4, 8, 5, 10, 9, 7, 3, 6};
        long egn = 0;
        String strCtrDigit = "";
        int digCtrDigit = 0;
        String strSubEgn = "";
        int digSubEgn = 0;
        String msg = null;

        try {
            if (part.length() == 10) {
                egn = Long.parseLong(part);
                strCtrDigit = part.substring(9, 10);
                digCtrDigit = Integer.parseInt(strCtrDigit);
                for (int i = 0; i < part.length() - 1; ++i) {
                    strSubEgn = part.substring(i, i + 1);
                    digSubEgn = Integer.parseInt(strSubEgn);
                    ctrSum += (teglo[i] * digSubEgn);
                }
                ctrField = (ctrSum % 11) % 10;
                if (ctrField == digCtrDigit) {
                    isPartValidEgn = true;
                } else {
                    isPartValidEgn = false;
                }
            }
        } catch (NumberFormatException e) {
            isPartValidEgn = false;
            msg = "<html><center><FONT COLOR=RED><b>•&nbsp;Невалиден числов формат!</b></FONT></center></html>";
            JOptionPane.showMessageDialog(f, msg, "Failure", JOptionPane.ERROR_MESSAGE);
        }

        return isPartValidEgn;
    }

    public Boolean checkIsPartCombineEgn(String part) {
        Boolean isPartCombineEgn = false;

        if ((part.matches(".+\\bегн\\.\\d+\\b"))
                || (part.matches(".+\\bегн\\d+\\b"))
                || (part.matches("\\bегн\\.\\d+\\b"))
                || (part.matches("\\bегн\\d+\\b"))
                || (part.matches("егн\\.\\d+\\b"))
                || (part.matches("егн\\d+\\b"))
                || (part.matches("егн\\.\\d+"))
                || (part.matches("егн\\d+"))
                || (part.matches(".+егн\\d{10}\\b"))
                || (part.matches(".+егн\\d{10}"))) {  // With/Without a word Before/After the EGN, With/Without a point after the EGN!
            isPartCombineEgn = true;
        } else {
            isPartCombineEgn = false;
        }

        return isPartCombineEgn;
    }

    public Boolean checkIsPartCombineBulstat(String part) {
        Boolean isPartCombineBulstat = false;

        if ((part.matches(".+\\bбул\\.\\d+\\b"))
                || (part.matches(".+\\bбул\\d+\\b"))
                || (part.matches("\\bбул\\.\\d+\\b"))
                || (part.matches("\\bбул\\d+\\b"))
                || (part.matches("бул\\.\\d+\\b"))
                || (part.matches("бул\\d+\\b"))
                || (part.matches("бул\\.\\d+"))
                || (part.matches("бул\\d+"))) {  // With/Without a word Before/After the BULSTAT, With/Without a point after the BULSTAT!
            isPartCombineBulstat = true;
        } else {
            isPartCombineBulstat = false;
        }

        return isPartCombineBulstat;
    }

    public Boolean checkIsPartCombineStringDigits(String part) {
        Boolean isPartCombineStringDigits = false;

        if ((part.matches(".+\\b[а-яА-Я]\\.\\d+\\b"))
                || (part.matches(".+\\b[а-яА-Я]\\d+\\b"))
                || (part.matches("\\b[а-яА-Я]\\.\\d+\\b"))
                || (part.matches("\\b[а-яА-Я]\\d+\\b"))
                || (part.matches("[а-яА-Я]\\.\\d+\\b"))
                || (part.matches("[а-яА-Я]\\d+\\b"))
                || (part.matches("[а-яА-Я]\\.\\d+"))
                || (part.matches("[а-яА-Я]\\d+"))
                || (part.matches("^[а-яА-Я0-9]+"))) {  // With/Without a word Before/After the [а-яА-Я], With/Without a point after the [а-яА-Я]!
            isPartCombineStringDigits = true;
        } else {
            isPartCombineStringDigits = false;
        }

        return isPartCombineStringDigits;
    }

    public Boolean checkIsTextBulstat(String part) {
        Boolean isTextBulstat = false;

        if ((part.matches("бул"))
                || (part.matches("бул\\."))) {  // Word is Bulstat!
            isTextBulstat = true;
        } else {  // Word is Not Bulstat!
            isTextBulstat = false;
        }

        return isTextBulstat;
    }

    public Boolean checkIsTextEgn(String part) {
        Boolean isTextEgn = false;

        if ((part.matches("\\bегн\\b"))) {  // Word is Egn!
            isTextEgn = true;
        } else {  // Word is Not Egn!
            isTextEgn = false;
        }

        return isTextEgn;
    }

    public String encryptionEgn(String secretMessage) {
        String encodedMessage = "";
        String decryptedMessage = "";
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        KeyFactory keyFactory = null;
        Cipher encryptCipher = null;
        Cipher decryptCipher = null;
        X509EncodedKeySpec publicKeySpec = null;
        PKCS8EncodedKeySpec privateKeySpec = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            encryptCipher = Cipher.getInstance("RSA");
            decryptCipher = Cipher.getInstance("RSA");

            File publicKeyFile = new File("key/sebra_public.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
            encryptCipher.init(Cipher.ENCRYPT_MODE, (java.security.Key) publicKey);
            byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);  // This is the encrypted string

            // File privateKeyFile = new File("key/sebra_private.key");
            // byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            // privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // privateKey = keyFactory.generatePrivate(privateKeySpec);
            // decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            // byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            // decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);  // This is the decryption string
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }

        return encodedMessage;
    }

    public String hashEncryptionEgn(String encodedMessage) {
        int hashIntEncodedMessage = 0;
        String hashEncodedMessage = "";

        try {
            hashIntEncodedMessage = Math.abs(encodedMessage.hashCode());
            hashEncodedMessage = Integer.toString(hashIntEncodedMessage);
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormat Exception: invalid input string");
        }

        return hashEncodedMessage;
    }

    public String getPartDigit(String part) {
        String resultPart = "";
        char char_part = '\0';
        boolean isNumber = false;

        for (int i = 0; i < part.length(); i++) {
            char_part = part.charAt(i);
            isNumber = Character.isDigit(char_part);
            if (isNumber == true) {
                resultPart += char_part;
            }
        }

        return resultPart;
    }

    public String encryptionIban(String ibanPart) {
        String encodedIban = "";
        String decryptedIban = "";
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        KeyFactory keyFactory = null;
        Cipher encryptCipher = null;
        Cipher decryptCipher = null;
        X509EncodedKeySpec publicKeySpec = null;
        PKCS8EncodedKeySpec privateKeySpec = null;

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            encryptCipher = Cipher.getInstance("RSA");
            decryptCipher = Cipher.getInstance("RSA");

            File publicKeyFile = new File("key/sebra_public.key");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
            encryptCipher.init(Cipher.ENCRYPT_MODE, (java.security.Key) publicKey);
            byte[] ibanPartBytes = ibanPart.getBytes(StandardCharsets.UTF_8);
            byte[] encryptIbanBytes = encryptCipher.doFinal(ibanPartBytes);
            encodedIban = Base64.getEncoder().encodeToString(encryptIbanBytes);  // This is the encrypted string

            // File privateKeyFile = new File("key/sebra_private.key");
            // byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            // privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // privateKey = keyFactory.generatePrivate(privateKeySpec);
            // decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            // byte[] decryptedIbanBytes = decryptCipher.doFinal(encryptedMessageBytes);
            // decryptedIban = new String(decryptedIbanBytes, StandardCharsets.UTF_8);  // This is the decryption string
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Sebra.class.getName()).log(Level.SEVERE, null, ex);
        }

        return encodedIban;
    }

    public String hashEncryptionIban(String encryptIban) {
        int hashIntEncodedIban = 0;
        String hashEncodedIban = "";

        try {
            hashIntEncodedIban = Math.abs(encryptIban.hashCode());
            hashEncodedIban = Integer.toString(hashIntEncodedIban);
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormat Exception: invalid input string");
        }

        return hashEncodedIban;
    }

    public void setDataGeneralStatisticsTextArea(String msg) {
        String newline = "\n";
        gsTextArea.append(msg + newline);
    }

    public void removeDataGeneralStatisticsTextArea() {
        gsTextArea.removeAll();
    }

    public void setStatusLabel(String msg) {
        statusLabel.setText(msg);
    }

    public void clearStatusLabel() {
        statusLabel.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu About;
    private javax.swing.JMenuItem choiceInDelRegNumFile;
    private javax.swing.JMenuItem choiceInDelSebraCodesFile;
    private javax.swing.JMenuItem choiceRegNumFile;
    private javax.swing.JMenuItem choiceSebraCodesFile;
    private javax.swing.JScrollPane generalSebraScrollPane;
    private javax.swing.JTextArea gsTextArea;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenu menuChoiceFile;
    private javax.swing.JMenu menuDeleteRegNum;
    private javax.swing.JMenu menuDeleteSebraCode;
    private javax.swing.JMenuItem menuInCsv;
    private javax.swing.JMenuItem menuOrgCsv;
    private javax.swing.JMenuBar sebraMenuBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
