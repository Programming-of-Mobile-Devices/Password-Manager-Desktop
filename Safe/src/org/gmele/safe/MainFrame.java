package org.gmele.safe;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.gmele.general.crypto.Crypto;
import org.gmele.general.exceptions.GmException;

/**
 *
 * @author gmele
 */
public final class MainFrame extends JFrame implements ActionListener, MouseListener
{

    DataHandler DH;
    JMenuBar Menu;
    JMenu MFile;
    JMenuItem MFKeys;
    JMenuItem MFOpen;
    JMenuItem MFSave;
    JMenuItem MFSaveAs;
    JMenuItem MFEncrypt;
    JMenuItem MFDecrypt;
    JMenuItem MFEncryptDir;
    JMenuItem MFDecryptDir;
    JMenuItem MFEncryptDirRec;
    JMenuItem MFDecryptDirRec;
    JMenuItem MFDestroy;
    JMenuItem MFExit;
    JMenu MEdit;
    JMenuItem MEInsert;
    JMenuItem MEInsert5;
    JMenuItem MEDelete;
    JMenuItem MEUp;
    JMenuItem MEDown;
    JMenuItem MECopy;
    JMenuItem MEPaste;
    JMenuItem MEClear;
    JMenuItem MESearch;
    JMenuItem PMEInsert;
    JMenuItem PMEInsert5;
    JMenuItem PMEDelete;
    JMenuItem PMEUp;
    JMenuItem PMEDown;
    JMenuItem PMECopy;
    JMenuItem PMEPaste;
    JMenuItem PMEClear;
    JMenuItem PMESearch;
    JScrollPane Scroll;
    JTable Grid;
    JPopupMenu PMenu;
    String CurFn;
    String Pass1;
    String Pass2;
    File LastDir;

    MainFrame (DataHandler Data)
    {
        DH = Data;
        setBounds (300, 200, 1000, 600);
        setResizable (true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setTitle ("Safe");
        URL IconName = getClass ().getResource ("resources/safe1.png");
        setIconImage (new ImageIcon (IconName).getImage ());
        //LastDir = new File (System.getProperty ("user.home"));
        LastDir = new File ("/home/Filekeep/Documents/Security");
        MakeMenus ();
        MakeGrid ();
        Scroll = new JScrollPane (Grid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Scroll.setWheelScrollingEnabled(true);
        setContentPane (Scroll);
        setVisible (true);
        CurFn = null;
        Pass1 = null;
        Pass2 = null;
        DoMenuKeys ();
    }

    void MakeMenus ()
    {
        Menu = new JMenuBar ();
        MFile = new JMenu ("Αρχείο");
        MFKeys = new JMenuItem ("Συνθηματικά");
        MFKeys.setAccelerator (KeyStroke.getKeyStroke ("control K"));
        MFKeys.addActionListener (this);
        MFile.add (MFKeys);
        MFile.addSeparator ();
        MFOpen = new JMenuItem ("Άνοιγμα");
        MFOpen.setAccelerator (KeyStroke.getKeyStroke ("control O"));
        MFOpen.addActionListener (this);
        MFile.add (MFOpen);
        MFSave = new JMenuItem ("Αποθήκευση");
        MFSave.setAccelerator (KeyStroke.getKeyStroke ("control S"));
        MFSave.addActionListener (this);
        MFile.add (MFSave);
        MFSaveAs = new JMenuItem ("Αποθήκευση Ως");
        MFSaveAs.addActionListener (this);
        MFile.add (MFSaveAs);
        MFile.add (new JSeparator ());
        MFEncrypt = new JMenuItem ("Κρυπτογράφηση Αρχείου");
        MFEncrypt.addActionListener (this);
        MFile.add (MFEncrypt);
        MFDecrypt = new JMenuItem ("Αποκρυπτογράφηση Αρχείου");
        MFDecrypt.addActionListener (this);
        MFile.add (MFDecrypt);
        MFEncryptDir = new JMenuItem ("Κρυπτογράφηση Καταλόγου");
        MFEncryptDir.addActionListener (this);
        MFile.add (MFEncryptDir);
        MFDecryptDir = new JMenuItem ("Αποκρυπτογράφηση Καταλόγου");
        MFDecryptDir.addActionListener (this);
        MFile.add (MFDecryptDir);
        MFEncryptDirRec = new JMenuItem ("Κρυπτογράφηση Καταλόγου (Αναδρομικά)");
        MFEncryptDirRec.addActionListener (this);
        MFile.add (MFEncryptDirRec);
        MFDecryptDirRec = new JMenuItem ("Αποκρυπτογράφηση Καταλόγου (Αναδρομικά)");
        MFDecryptDirRec.addActionListener (this);
        MFile.add (MFDecryptDirRec);
        MFDestroy = new JMenuItem ("Καταστροφή Αρχείου");
        MFDestroy.addActionListener (this);
        MFile.add (MFDestroy);
        MFile.add (new JSeparator ());
        MFExit = new JMenuItem ("Έξοδος");
        MFExit.setAccelerator (KeyStroke.getKeyStroke ("control X"));
        MFExit.addActionListener (this);
        MFile.add (MFExit);
        MEdit = new JMenu ("Λειτουργίες");
        MEInsert = new JMenuItem ("Εισαγωγή Γραμμής");
        MEInsert.addActionListener (this);
        MEdit.add (MEInsert);
        MEInsert5 = new JMenuItem ("Εισαγωγή 5 Γραμμών");
        MEInsert5.addActionListener (this);
        MEdit.add (MEInsert5);
        MEDelete = new JMenuItem ("Διαγραφή Γραμμής");
        MEDelete.addActionListener (this);
        MEdit.add (MEDelete);
        MEUp = new JMenuItem ("Μεταφορά Επάνω");
        MEUp.setAccelerator (KeyStroke.getKeyStroke ("control U"));
        MEUp.addActionListener (this);
        MEdit.add (MEUp);
        MEDown = new JMenuItem ("Μεταφορά Κάτω");
        MEDown.setAccelerator (KeyStroke.getKeyStroke ("control D"));
        MEDown.addActionListener (this);
        MEdit.add (MEDown);
        MEdit.add (new JSeparator ());
        MECopy = new JMenuItem ("Αντιγραφή");
        MECopy.setAccelerator (KeyStroke.getKeyStroke ("control C"));
        MECopy.addActionListener (this);
        MEdit.add (MECopy);
        MEPaste = new JMenuItem ("Επικόλληση");
        MEPaste.setAccelerator (KeyStroke.getKeyStroke ("control P"));
        MEPaste.addActionListener (this);
        MEdit.add (MEPaste);
        MEClear = new JMenuItem ("Καθαρισμός");
        MEClear.addActionListener (this);
        MEdit.add (MEClear);
        MEdit.add (new JSeparator ());
        MESearch = new JMenuItem ("Αναζήτηση");
        MESearch.addActionListener (this);
        MEdit.add (MESearch);
        Menu.add (MFile);
        Menu.add (MEdit);
        setJMenuBar (Menu);
        PMenu = new JPopupMenu ();
        PMEInsert = new JMenuItem ("Εισαγωγή Γραμμής");
        PMEInsert.addActionListener (this);
        PMenu.add (PMEInsert);
        PMEInsert5 = new JMenuItem ("Εισαγωγή 5 Γραμμών");
        PMEInsert5.addActionListener (this);
        PMenu.add (PMEInsert5);
        PMEDelete = new JMenuItem ("Διαγραφή Γραμμής");
        PMEDelete.addActionListener (this);
        PMenu.add (PMEDelete);
        PMEUp = new JMenuItem ("Μεταφορά Επάνω");
        PMEUp.setAccelerator (KeyStroke.getKeyStroke ("control U"));
        PMEUp.addActionListener (this);
        PMenu.add (PMEUp);
        PMEDown = new JMenuItem ("Μεταφορά Κάτω");
        PMEDown.setAccelerator (KeyStroke.getKeyStroke ("control D"));
        PMEDown.addActionListener (this);
        PMenu.add (PMEDown);
        PMenu.add (new JSeparator ());
        PMECopy = new JMenuItem ("Αντιγραφή");
        PMECopy.setAccelerator (KeyStroke.getKeyStroke ("control C"));
        PMECopy.addActionListener (this);
        PMenu.add (PMECopy);
        PMEPaste = new JMenuItem ("Επικόλληση");
        PMEPaste.setAccelerator (KeyStroke.getKeyStroke ("control P"));
        PMEPaste.addActionListener (this);
        PMenu.add (PMEPaste);
        PMEClear = new JMenuItem ("Καθαρισμός");
        PMEClear.addActionListener (this);
        PMenu.add (PMEClear);
        PMenu.add (new JSeparator ());
        PMESearch = new JMenuItem ("Αναζήτηση");
        PMESearch.addActionListener (this);
        PMenu.add (PMESearch);
        
        

    }

    void MakeGrid ()
    {
        Grid = new JTable (DH);
        Grid.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
        Grid.getColumnModel ().getColumn (0).setMinWidth (130);
        Grid.getColumnModel ().getColumn (1).setMinWidth (120);
        Grid.getColumnModel ().getColumn (2).setMinWidth (130);
        Grid.getColumnModel ().getColumn (3).setMinWidth (120);
        Grid.getColumnModel ().getColumn (4).setMinWidth (120);
        Grid.getColumnModel ().getColumn (5).setMinWidth (120);
        Grid.getColumnModel ().getColumn (6).setMinWidth (120);
        Grid.getColumnModel ().getColumn (7).setMinWidth (170);
        Grid.setDefaultRenderer (String.class, new MyRenderer ());
        Grid.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        Grid.setRowSelectionInterval (0, 0);
        Grid.addMouseListener (this);
       
    }

    @Override
    public void actionPerformed (ActionEvent e)
    {
        Object M = e.getSource ();
        if (M == MFKeys)
        {
            DoMenuKeys ();
        }
        if (M == MFOpen)
        {
            DoMenuLoad ();
        }
        if (M == MFSave)
        {
            DoMenuSave (false);
        }
        if (M == MFSaveAs)
        {
            DoMenuSave (true);
        }
        if (M == MFEncrypt)
        {
            DoMenuEncrypt ();
        }
        if (M == MFDecrypt)
        {
            DoMenuDecrypt ();
        }
        if (M == MFEncryptDir)
        {
            DoMenuEncryptDir (false);
        }
        if (M == MFDecryptDir)
        {
            DoMenuDecryptDir (false);
        }
        if (M == MFEncryptDirRec)
        {
            DoMenuEncryptDir (true);
        }
        if (M == MFDecryptDirRec)
        {
            DoMenuDecryptDir (true);
        }
        if (M == MFDestroy)
        {
            DoMenuDestroy ();
        }
        if (M == MFExit)
        {
            DoMenuExit ();
        }
        if (M == MEInsert || M == PMEInsert)
        {
            DoInsertRow ();
        }
        if (M == MEInsert5 || M == PMEInsert5)
        {
            DoInsert5Rows ();
        }
        if (M == MEDelete || M == PMEDelete)
        {
            DoDeleteRow ();
        }
        if (M == MEUp || M == PMEUp)
        {
            DoMoveUp ();
        }
        if (M == MEDown || M == PMEDown)
        {
            DoMoveDown ();
        }
        if (M == MECopy || M == PMECopy)
        {
            DoCopy ();
        }
        if (M == MEPaste || M == PMEPaste)
        {
            DoPaste ();
        }
        if (M == MEClear || M == PMEClear)
        {
            DoClear ();
        }
        if (M == MESearch || M == PMESearch)
        {
            DoSearch ();
        }
    }

    void DoMenuKeys ()
    {
        PasswordDialog PD = new PasswordDialog ();
        do
        {
            boolean R = PD.Show ();
            if (R)
            {
                Pass1 = PD.Pass1;
                Pass2 = PD.Pass2;
            }
        }
        while (Pass1 == null || Pass2 == null);
    }

    void DoMenuLoad ()
    {
        if (DH.IsChanged ())
        {
            int Res = JOptionPane.showConfirmDialog (this, "Δεν έχουν αποθηκευτεί οι αλλαγές στο τρέχον αρχείο.\n"
                + "Θέλετε να ανοίξετε άλλο αρχείο;", "Άνοιγμα Αρχείου", JOptionPane.YES_NO_OPTION);
            if (Res != JOptionPane.YES_OPTION)
            {
                return;
            }
        }
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        FileNameExtensionFilter flt = new FileNameExtensionFilter ("Encrypted Keys File (*.ekf)", "ekf");
        Fc.setFileFilter (flt);
        Fc.setDialogTitle ("Άνοιγμα Αρχείου Κλειδιών");
        Fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        int result = Fc.showOpenDialog (this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            LastDir = Fc.getSelectedFile ().getParentFile ();
            File Sel = Fc.getSelectedFile ();
            if (!DH.LoadData (Sel.getAbsolutePath (), Pass1, Pass2))
            {
                ShowMessage ("Άνοιγμα Αρχείου Κλειδιών", "Το άνοιγμα του αρχείου κλειδιών απέτυχε. Οι πιθανές αιτίες είναι:\n"
                    + "    Το αρχείο δεν μπορεί να διαβαστεί.\n    Το αρχείο έχει άκυρο περιεχόμενο.\n    Τα συνθηματικά είναι λανθασμένα.",
                    JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                CurFn = Sel.getAbsolutePath ();
                setTitle ("Safe - " + Sel.getName ());
                Grid.clearSelection ();
                Rectangle cellRect = Grid.getCellRect (0, 0, true);
                Grid.scrollRectToVisible (cellRect);
            }
        }
    }

    void DoMenuSave (boolean SaveAs)
    {
        String TmpFullFn = CurFn;
        String TitleFn = null;
        if (!DH.IsChanged () && !SaveAs)
        {
            return;
        }
        if (SaveAs || CurFn == null)
        {
            JFileChooser Fc = new JFileChooser ();
            Fc.setCurrentDirectory (LastDir);
            FileNameExtensionFilter flt = new FileNameExtensionFilter ("Encrypted Keys File (*.ekf)", "ekf");
            Fc.setFileFilter (flt);
            Fc.setDialogTitle ("Αποθήκευση Αρχείου Κλειδιών");
            Fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
            int result = Fc.showSaveDialog (this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                LastDir = Fc.getSelectedFile ().getParentFile ();
                TmpFullFn = Fc.getSelectedFile ().getAbsolutePath ();
                if (!TmpFullFn.endsWith (".ekf"))
                {
                    TmpFullFn += ".ekf";
                }
            }
            else
            {
                TmpFullFn = null;
            }
        }
        if (TmpFullFn == null)
        {
            return;
        }
        if (DH.SaveData (TmpFullFn, Pass1, Pass2))
        {
            CurFn = TmpFullFn;
            TitleFn = new File(TmpFullFn).getName ();
            setTitle ("Safe - " + TitleFn);

        }
        else
        {
            ShowMessage ("Αποθήκευση Αρχείου Κλειδιών", "Η αποθήκευση του αρχείου κλειδιών απέτυχε. Οι πιθανές αιτίες είναι:\n"
                + "    Το αρχείο δεν μπορεί να δημιουργηθεί / τροποποιηθεί.\n    Δημιουργήθηκε λάθος κατά την κρυπτογράφηση.\n",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    void DoMenuEncrypt ()
    {
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        Fc.setDialogTitle ("Επιλογή Αρχείου προς Κρυπτογράφηση");
        Fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        if (Fc.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        LastDir = Fc.getSelectedFile ().getParentFile ();
        String SourceFn = Fc.getSelectedFile ().getAbsolutePath ();
        if (!Fc.getSelectedFile ().exists ())
        {
            ShowMessage ("Λάθος Αρχείο", "Το προς κρυπτογράφηση αρχείο που επιλέξατε δεν υπάρχει.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Fc.setDialogTitle ("Επιλογή Κρυπτογραφημένου Αρχείου");
        Fc.setSelectedFile (new File (SourceFn + ".ef"));
        FileNameExtensionFilter flt = new FileNameExtensionFilter ("Encrypted File (*.ef)", "ef");
        Fc.setFileFilter (flt);
        Fc.setAcceptAllFileFilterUsed (false);
        if (Fc.showSaveDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        String DestFn = Fc.getSelectedFile ().getAbsolutePath ();
        if (DestFn.lastIndexOf ('.') == -1)
        {
            DestFn += ".ef";
        }
        if (new File (DestFn).exists ())
        {
            ShowMessage ("Λάθος Αρχείο", "Υπάρχει ήδη αρχείο με αυτό το όνομα.\nΕπιλέξτε όνομα αρχείου που δεν υπάρχει",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        try
        {
            Crypto.EncryptFile (SourceFn, DestFn, Pass1, Pass2);
            ShowMessage ("Επιτυχής Κρυπτογράφηση.", "Η κρυπτογράφηση του αρχείου ολοκληρώθηκε με επιτυχία.", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (GmException e)
        {
            System.out.println (e.getMessage () + " " + e.getData ());
            int En = e.getErrorCode ();
            switch (En)
            {
                case GmException.CryptCannotReadFile:
                    ShowMessage ("Πρόβλημα στο Πηγαίο  Αρχείο", "Το προς κρυπτογράφηση αρχείο δεν μπορεί να διαβαστεί.",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case GmException.CryptCannotCreateFile:
                case GmException.CryptCannotWriteFile:
                    ShowMessage ("Πρόβλημα στο Αρχείο Προορισμού", "Το κρυπτογραφημένο αρχείο δεν μπορεί να δημιουργηθεί / γραφτεί.",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    ShowMessage ("Απρόσμενο λάθος", "Δημιουργήθηκε απρόσμενο λάθος κατα την κρυπτογράφηση.\nΚωδικός λάθους: " + En,
                        JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        setCursor (Cursor.getDefaultCursor ());
    }

    void DoMenuDecrypt ()
    {
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        Fc.setDialogTitle ("Επιλογή Κρυπτογραφημένου Αρχείου");
        Fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        FileNameExtensionFilter flt = new FileNameExtensionFilter ("Encrypted File (*.ef)", "ef");
        Fc.setFileFilter (flt);
        if (Fc.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        LastDir = Fc.getSelectedFile ().getParentFile ();
        String SourceFn = Fc.getSelectedFile ().getAbsolutePath ();
        if (!Fc.getSelectedFile ().exists ())
        {
            ShowMessage ("Λάθος Αρχείο", "Το προς αποκρυπτογράφηση αρχείο που επιλέξατε δεν υπάρχει.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String DestFn = SourceFn;
        if (DestFn.endsWith (".ef"))
        {
            DestFn = DestFn.substring (0, DestFn.length () - 3);
        }
        Fc.setDialogTitle ("Επιλογή ΑποΚρυπτογραφημένου Αρχείου");
        Fc.setSelectedFile (new File (DestFn));
        Fc.setFileFilter (null);
        Fc.setAcceptAllFileFilterUsed (true);
        if (Fc.showSaveDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        DestFn = Fc.getSelectedFile ().getAbsolutePath ();
        if (new File (DestFn).exists ())
        {
            ShowMessage ("Λάθος Αρχείο", "Υπάρχει ήδη αρχείο με αυτό το όνομα.\nΕπιλέξτε όνομα αρχείου που δεν υπάρχει",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        try
        {
            Crypto.DecryptFile (SourceFn, DestFn, Pass1, Pass2);
            ShowMessage ("Επιτυχής Αποκρυπτογράφηση.", "Η αποκρυπτογράφηση του αρχείου ολοκληρώθηκε με επιτυχία.",
                JOptionPane.INFORMATION_MESSAGE);
        }
        catch (GmException e)
        {
            System.out.println (e.getMessage () + " " + e.getData ());
            int En = e.getErrorCode ();
            switch (En)
            {
                case GmException.CryptCannotReadFile:
                    ShowMessage ("Πρόβλημα στο Πηγαίο  Αρχείο", "Το προς αποκρυπτογράφηση αρχείο δεν μπορεί να διαβαστεί.",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case GmException.CryptCannotCreateFile:
                case GmException.CryptCannotWriteFile:
                    ShowMessage ("Πρόβλημα στο Αρχείο Προορισμού", "Το αποκρυπτογραφημένο αρχείο δεν μπορεί να δημιουργηθεί / γραφτεί.",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case GmException.CryptInvalidPassword:
                    ShowMessage ("Λανθασμένα Συνθηματικά", "Η αποκρυπτογράφηση του αρχείου δεν είναι δυνατή.\nΚάποιο από τα δύο "
                        + "συνθηματικά είναι λανθασμένο.", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    ShowMessage ("Απρόσμενο λάθος", "Δημιουργήθηκε απρόσμενο λάθος κατα την κρυπτογράφηση.\nΚωδικός λάθους: " + En,
                        JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        setCursor (Cursor.getDefaultCursor ());
    }

    void DoMenuEncryptDir (boolean R)
    {
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        Fc.setDialogTitle ("Επιλογή Καταλόγου προς Κρυπτογράφηση");
        Fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        if (Fc.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        LastDir = Fc.getSelectedFile ().getParentFile ();
        File RootDir = Fc.getSelectedFile ();
        System.out.println (RootDir.getAbsolutePath ());
        ArrayList<File> AllFiles = new ArrayList ();
        MakeFileList (RootDir, AllFiles, R);
        int i = 0;
        for (File f : AllFiles)
        {
            i++;
            String SourceFn = f.getAbsolutePath ();
            System.out.println (SourceFn + " (" + i + " / " + AllFiles.size () + ")");
            if (SourceFn.endsWith (".ef") || SourceFn.endsWith (".ekf"))
            {
                continue;
            }
            String DestFn = SourceFn + ".ef";
            if (new File (DestFn).exists ())
            {
                continue;
            }
            try
            {
                setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
                Crypto.EncryptFile (SourceFn, DestFn, Pass1, Pass2);
            }
            catch (GmException e)
            {
                System.out.println (e.getMessage () + " " + e.getData ());
                int En = e.getErrorCode ();
                switch (En)
                {
                    case GmException.CryptCannotReadFile:
                        ShowMessage ("Πρόβλημα στο Πηγαίο Αρχείο: ", "Το αρχείο " + SourceFn + " δεν μπορεί να διαβαστεί.",
                            JOptionPane.ERROR_MESSAGE);
                        break;
                    case GmException.CryptCannotCreateFile:
                    case GmException.CryptCannotWriteFile:
                        ShowMessage ("Πρόβλημα στο Αρχείο Προορισμού: ", "Το αρχείο " + DestFn + " δεν μπορεί να δημιουργηθεί / γραφτεί.",
                            JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        ShowMessage ("Απρόσμενο λάθος", "Δημιουργήθηκε απρόσμενο λάθος κατα την κρυπτογράφηση του " + SourceFn + ".\n"
                            + "Κωδικός λάθους: " + En,
                            JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        }
        setCursor (Cursor.getDefaultCursor ());
    }

    void DoMenuDecryptDir (boolean R)
    {
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        Fc.setDialogTitle ("Επιλογή Καταλόγου προς Αποκρυπτογράφηση");
        Fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        if (Fc.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        LastDir = Fc.getSelectedFile ().getParentFile ();
        File RootDir = Fc.getSelectedFile ();
        System.out.println (RootDir.getAbsolutePath ());
        ArrayList<File> AllFiles = new ArrayList ();
        MakeFileList (RootDir, AllFiles, R);
        int i = 0;
        for (File f : AllFiles)
        {
            i++;
            String SourceFn = f.getAbsolutePath ();
            System.out.println (SourceFn + " (" + i + " / " + AllFiles.size () + ")");
            if (!SourceFn.endsWith (".ef"))
            {
                continue;
            }
            String DestFn = SourceFn.substring (0, SourceFn.lastIndexOf (".ef"));
            if (new File (DestFn).exists ())
            {
                continue;
            }
            try
            {
                setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
                Crypto.DecryptFile (SourceFn, DestFn, Pass1, Pass2);
            }
            catch (GmException e)
            {
                System.out.println (e.getMessage () + " " + e.getData ());
                int En = e.getErrorCode ();
                switch (En)
                {
                    case GmException.CryptCannotReadFile:
                        ShowMessage ("Πρόβλημα στο Πηγαίο Αρχείο: ", "Το αρχείο " + SourceFn + " δεν μπορεί να διαβαστεί.",
                            JOptionPane.ERROR_MESSAGE);
                        break;
                    case GmException.CryptCannotCreateFile:
                    case GmException.CryptCannotWriteFile:
                        ShowMessage ("Πρόβλημα στο Αρχείο Προορισμού: ", "Το αρχείο " + DestFn + " δεν μπορεί να δημιουργηθεί / γραφτεί.",
                            JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        ShowMessage ("Απρόσμενο λάθος", "Δημιουργήθηκε απρόσμενο λάθος κατα την αποκρυπτογράφηση του " + SourceFn + ".\n"
                            + "Κωδικός λάθους: " + En,
                            JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        }
        setCursor (Cursor.getDefaultCursor ());
    }

    void DoMenuDestroy ()
    {
        JFileChooser Fc = new JFileChooser ();
        Fc.setCurrentDirectory (LastDir);
        Fc.setDialogTitle ("Επιλογή Αρχείου προς Καταστροφή");
        Fc.setFileSelectionMode (JFileChooser.FILES_ONLY);
        if (Fc.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        LastDir = Fc.getSelectedFile ().getParentFile ();
        String SourceFn = Fc.getSelectedFile ().getAbsolutePath ();
        if (!Fc.getSelectedFile ().exists ())
        {
            ShowMessage ("Λάθος Αρχείο", "Το προς καταστροφή αρχείο που επιλέξατε δεν υπάρχει.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int Res = JOptionPane.showConfirmDialog (this, "Θέλετε, σίγουρα, να καταστρέψετε το αρχείο: " + SourceFn, "Καταστροφή Αρχείου",
            JOptionPane.YES_NO_OPTION);
        if (Res != JOptionPane.YES_OPTION)
        {
            return;
        }
        setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        try
        {
            Crypto.DestroyFile (SourceFn);
            ShowMessage ("Επιτυχής Καταστροφή", "Η καταστροφή του αρχείου ολοκληρώθηκε με επιτυχία.", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (GmException e)
        {
            int En = e.getErrorCode ();
            switch (En)
            {
                case GmException.CryptCannotReadFile:
                    ShowMessage ("Καταστροφή Αρχείου", "Το προς καταστροφή αρχείο δεν υπάρχει.", JOptionPane.ERROR_MESSAGE);
                    break;
                case GmException.CryptCannotWriteFile:
                    ShowMessage ("Καταστροφή Αρχείου", "Το προς καταστροφή αρχείο δεν μπορεί να... καταστραφεί.",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    ShowMessage ("Καταστροφή Αρχείου", "Δημιουργήθηκε απρόσμενο λάθος κατα την καταστροφή.\nΚωδικός λάθους: " + En,
                        JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        setCursor (Cursor.getDefaultCursor ());
    }

    void DoMenuExit ()
    {
        if (DH.IsChanged ())
        {
            int Res = JOptionPane.showConfirmDialog (this, "Δεν έχουν αποθηκευτεί οι αλλαγές στο τρέχον αρχείο.\n"
                + "Θέλετε να τερματίσετε το πρόγραμμα;", "Τερματισμός Προγράμματος", JOptionPane.YES_NO_OPTION);
            if (Res != JOptionPane.YES_OPTION)
            {
                return;
            }
        }
        dispose ();

    }

    void DoInsertRow ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row == -1)
        {
            return;
        }
        JViewport Viewport = Scroll.getViewport ();
        Point P = Viewport.getViewPosition ();
        final int Fr = Grid.rowAtPoint (P);
        DH.InsertRow (Row);
        SwingUtilities.invokeLater (new Runnable ()
        {
            @Override
            public void run() 
            {
                Grid.setRowSelectionInterval (Row, Row);
                Rectangle Rect = Grid.getCellRect (Fr, 0, true);
                Grid.scrollRectToVisible(Rect);
            }
        });
        
        
        
        
    }
    
    void DoInsert5Rows ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row == -1)
        {
            return;
        }
        JViewport Viewport = Scroll.getViewport ();
        Point P = Viewport.getViewPosition ();
        final int Fr = Grid.rowAtPoint (P);
        DH.InsertRows(Row, 5);
        SwingUtilities.invokeLater (new Runnable ()
        { 
            @Override
            public void run() 
            {
                Grid.setRowSelectionInterval (Row, Row);
                Rectangle Rect = Grid.getCellRect (Fr, 0, true);
                Grid.scrollRectToVisible(Rect);
            }
        });
    }
    
    void DoDeleteRow ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row == -1 || DH.getRowCount () < 4)
        {
            return;
        }
        int Res = JOptionPane.showConfirmDialog (this, "Θέλετε να διαγραφεί η επιλεγμένη εγγραφή;", "Διαγραφή Εγγραφής",
            JOptionPane.YES_NO_OPTION);
        if (Res == JOptionPane.YES_OPTION)
        {
            DH.DeleteRow (Row);
        }
    }

    void DoMoveUp ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row > 0)
        {
            if (Grid.isEditing ())
                Grid.getCellEditor().stopCellEditing();
            Grid.clearSelection ();
            DH.MoveUp (Row--);
            Grid.setRowSelectionInterval (Row, Row);  
        }

    }

    void DoMoveDown ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row != -1 && Row < DH.getRowCount () - 1)
        {
            if (Grid.isEditing ())
                Grid.getCellEditor().stopCellEditing();
            Grid.clearSelection ();
            DH.MoveDown (Row++);
            Grid.setRowSelectionInterval (Row, Row);
        }
    }
    
    void DoCopy ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row != -1)
            DH.CopyRow (Row);
    }
    
    void DoPaste ()
    {
        int Row = Grid.getSelectedRow ();
        if (Row != -1)
        {
            JViewport viewport = Scroll.getViewport ();
            Point p = viewport.getViewPosition ();
            int Fr = Grid.rowAtPoint (p);      
            DH.PasteRow (Row);
            SwingUtilities.invokeLater (new Runnable ()
            { 
                @Override
                public void run() 
                {
                    Grid.setRowSelectionInterval (Row, Row);
                    Rectangle Rect = Grid.getCellRect (Fr, 0, true);
                    Grid.scrollRectToVisible(Rect);
                }
            });
            
        }
    }
    
    void DoClear ()
    {
        int Row = Grid.getSelectedRow (); 
        if (Row != -1)
            DH.ClearRow (Row);
    }
    
    void DoSearch ()
    {
        String TBS = JOptionPane.showInputDialog("What to Search : ","");
    }

    void ShowMessage (String Title, String Message, int Type)
    {
        JOptionPane.showMessageDialog (this, Message, Title, Type);
    }

    
    @Override
    public void mouseClicked (MouseEvent e)
    {
        if (e.getButton () == MouseEvent.BUTTON3)
        {
            PMenu.show (Grid, e.getX (), e.getY ());
        }
    }

    @Override
    public void mousePressed (MouseEvent e)
    {
        //throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased (MouseEvent e)
    {
        //throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered (MouseEvent e)
    {
        //throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited (MouseEvent e)
    {
        //throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void MakeFileList (File RootDir, ArrayList<File> AllFiles, boolean R)
    {
        File[] FList = RootDir.listFiles ();
        for (File file : FList)
        {
            if (file.isFile ())
            {
                AllFiles.add (file);
            }
            else if (file.isDirectory () && R)
            {
                MakeFileList (file.getAbsoluteFile (), AllFiles, R);
            }
        }
    }
}

class MyRenderer extends JTextPane implements TableCellRenderer
{

    MyRenderer ()
    {
        //setLineWrap (true);
    }

    @Override
    public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        String Val = (String) value;
        setText ((String) value);
        if (isSelected)
        {
            setBackground (UIManager.getColor ("Table.selectionBackground"));
        }
        else
        {
            setBackground (UIManager.getColor ("Table.background"));
        }
        int W = table.getColumnModel ().getColumn (column).getWidth ();
        int H = getContentHeight (W, Val);
        setPreferredSize (new Dimension (W, H));
        if (H > table.getRowHeight (row))
        {
            table.setRowHeight (row, H);
        }
        //Added lines
        StyledDocument doc = this.getStyledDocument ();
        SimpleAttributeSet center = new SimpleAttributeSet ();
        StyleConstants.setAlignment (center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove (center, StyleConstants.getSpaceBelow (center) / 2);
        StyleConstants.setSpaceBelow (center, StyleConstants.getSpaceAbove (center));
        doc.setParagraphAttributes (0, doc.getLength (), center, false);
        //Added Lines
        return this;
    }

    private int getContentHeight (int width, String content)
    {
        JEditorPane dummyEditorPane = new JEditorPane ();
        dummyEditorPane.setSize (width, Short.MAX_VALUE);
        dummyEditorPane.setText (content);
        return dummyEditorPane.getPreferredSize ().height;
    }
}
