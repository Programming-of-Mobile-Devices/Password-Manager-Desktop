
package org.gmele.general.crypto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.gmele.general.exceptions.GmException;

/**
 * Η κλάση παρέχει βασικές λειτουργίες κρυπτογράφησης. Περιλαμβάνει δημιουργία κλειδιού, κρυπτογράφηση πίνακα και αρχείου, καταστροφή 
 * αρχείου στο δίσκο, κλπ. Οι λειτουργίες της κρυπτογράφησης και της αποκρυπτογράφησης αρχείου γίνονται με μεθόδους που εκτελούνται στο
 * σύνολο του αρχείου συνολικά (αν χωρά στη μνήμη ολόκληρο) και με μεθόδους που εκτελούν τη λειτουργία block με block.
 * @author gmele
 * @version 1.5
 */
public class Crypto
{
    /**
     * Μετατρέπει ένα UTF-8 string σε 128 bits (16 bytes) κλειδί για χρήση σε αλγόριθμους συμμετρικής κρυπτογράφησης. Το string μετατρέπεται
     * σε πίνακα bytes και στην συνέχεια εφαρμόζεται πάνω του ο hash αλγόριθμος SHA-1. Από τον πίνακα που παράγεται επιστρέφονται τα πρώτα 
     * 16 bytes. 
     * @param Password Το string (συνθηματικό) που θα μετατραπεί σε κλειδί.
     * @return Το 128 bit κλειδί.
     * @throws GmException Λάθος στις παραμέτρους των βιβλιοθηκών. Δεν πρέπει να συμβεί ποτέ.
     */
    public static byte[] Password2Key (String Password) throws GmException
    {
        try
        {
            byte[] Key = Password.getBytes ("UTF-8");
            MessageDigest sha = MessageDigest.getInstance ("SHA-1");
            Key = sha.digest (Key);
            Key = Arrays.copyOf (Key, 16); 
            return Key;
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException ex)
        {
            throw new GmException ("Password2Key", GmException.GenImpossibleException, Password, ex);
        }   
    } 
    
    /**
     * Κρυπτογραφεί / Αποκρυπτογραφεί ένα πίνακα με bytes εφαρμόζοντας τον αλγόριθμο XOR με ένα UTF-8 password. Το συνθηματικό μετατρέπεται
     * σε πίνακα από bytes. Δεν μπορεί να διαπιστωθεί αν το κλειδί είναι σωστό και αν η αποκρυπτογράφηση πραγματοποιήθηκε κανονικά. 
     * @param Mat Ο πίνακας προς κρυπτογράφηση / αποκρυπτογράφηση.
     * @param Password Το συνθηματικό κρυπτογράφησης / αποκρυπτογράφησης.
     * @return Ο πίνακας με το αποτέλεσμα της λειτουργίας.
     * @throws GmException Λάθος στις παραμέτρους των βιβλιοθηκών. Δεν πρέπει να συμβεί ποτέ.
     */
    public static byte[] XORCrypt (byte[] Mat, String Password) throws GmException 
    {
        byte[] Key = null;
        try
        {
            Key = Password.getBytes ("UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new GmException ("XORCrypt", GmException.GenImpossibleException, Password, ex);
        }
        int j = 0;
        byte[] Res = new byte[Mat.length];
        for (int i = 0; i < Mat.length; i++)
        {
            Res[i] = (byte) (Mat[i] ^ Key[j++]);
            if (j == Key.length)
                j = 0;
        }
        return Res;            
    }
    
    /**
     * Κρυπτογραφεί ένα πίνακα από bytes με τον αλγόριθμο AES και χρήση των βιβλιοθηκών της Java.Οι παράμετροι της κλάσης "Cipher" είναι:
     * "AES/CBC/PKCS5Padding". Ο πίνακας IvSpec για το CBC αν δεν οριστεί παραμετρικά ορίζεται στατικά εσωτερικά και πρέπει να θεωρείται... 
     * γνωστός.  Είναι ανασφαλές και μένει για λόγους συμβατότητας με την προηγούμενη έκδοση της βιβλιοθήκης. Το  συνθηματικό μετατρέπεται
     * σε κλειδί με την χρήση της {@link #Password2Key(java.lang.String)}. Κάθε κλήση της συνάρτησης θεωρείται νέα κρυπτογράφηση και όχι
     * συνέχεια της προηγούμενης. Λόγω του PKCS5 padding ο πίνακας που δημιουργείται έχει μέγεθος ίσο με το επόμενο πολλαπλάσιο του 16 από
     * το μέγεθος του πίνακα προς κρυπτογράφηση.
     * @param PlainMat Ο πίνακας που θα κρυπτογραφηθεί.  
     * @param Pass Το συνθηματικό της κρυπτογράφησης. 
     * @param Iv IvSpec matrix. Αν είναι null θα χρησιμοποιηθεί ο πίνακας της προηγούμενης έκδοσης της βιβλιοθήκης.
     * @return Ο κρυπτογραφημένος πίνακας  
     * @throws GmException Λάθος στις παραμέτρους των βιβλιοθηκών. Δεν πρέπει να συμβεί ποτέ.
     */
    public static byte[] AESEncrypt (byte[] PlainMat, String Pass, byte[] Iv) throws GmException
    {
        if (Iv == null)
        {
            byte[] IvSt = {45, 26, 11, 120, 32, 0, 1, 12, 1, 65, 9, 10, 34, 45, 2, 2};
            Iv = IvSt;
        }
        IvParameterSpec IvSpec = new IvParameterSpec (Iv);
        byte[] Key;
        Cipher c;
        try
        {
            Key = Password2Key (Pass);
            c = Cipher.getInstance ("AES/CBC/PKCS5Padding");
            SecretKeySpec k = new SecretKeySpec (Key, "AES");
            c.init (Cipher.ENCRYPT_MODE, k, IvSpec);
            byte[] CipherMat = c.doFinal (PlainMat);
            return CipherMat;
        }
        catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |InvalidAlgorithmParameterException | 
            IllegalBlockSizeException | BadPaddingException ex)
        {
            throw new GmException ("AESEncrypt", GmException.GenImpossibleException, Pass, ex);
        }
    }
    
    /**
     * Αποκρυπτογραφεί ένα πίνακα από bytes με τον αλγόριθμο AES και χρήση των βλιβλιοθηκών της Java.Οι παράμετροι της κλάσης "Cipher"
     * είναι: "AES/CBC/PKCS5Padding". Ο πίνακας IvSpec για το CBC αν δεν οριστεί παραμετρικά, είναι στατικά ορισμένος εσωτερικά και πρέπει
     * να θεωρείται... γνωστός. Το συνθηματικό μετατρέπεται σε κλειδί με την χρήση της {@link #Password2Key(java.lang.String)}. Κάθε κλήση
     * της συνάρτησης θεωρείται νέα αποκρυπτογράφηση και όχι συνέχεια της προηγούμενης. Λόγω του PKCS5 padding η μέθοδος μπορεί να 
     * διαπιστώσει αν η αποκρυπτογράφηση πραγματοποιήθηκε σωστά.
     * @param CipherMat Ο πίνακας που θα αποκρυπτογραφηθεί.
     * @param Pass Το συνθηματικό της αποκρυπτογράφησης.
     * @param Iv Iv matrix. Αν είναι null θα χρησιμοποιηθεί ο πίνακας της προηγούμενης έκδοσης της βιβλιοθήκης.
     * @return Ο πίνακας με τα αρχικά δεδομένα.
     * @throws GmException Σε περίπτωση λάθους συνθηματικού ή κρυπτογραφημένου πίνακα και σε περίπτωση λάθους παραμέτρων στις βιβλιοθήκες 
     * (δεν πρέπει να συμβεί ποτέ)
     */
    public static byte[] AESDecrypt (byte[] CipherMat, String Pass, byte[] Iv) throws GmException
    {
        if (Iv == null)
        {
            byte[] IvSt = {45, 26, 11, 120, 32, 0, 1, 12, 1, 65, 9, 10, 34, 45, 2, 2};
            Iv = IvSt;
        }
        IvParameterSpec IvSpec = new IvParameterSpec (Iv);
        byte[] Key;
        Cipher c;
        try
        {
            Key = Password2Key (Pass);
            c = Cipher.getInstance ("AES/CBC/PKCS5Padding");
            SecretKeySpec k = new SecretKeySpec (Key, "AES");
            c.init (Cipher.DECRYPT_MODE, k, IvSpec);
            byte[] PlainMat = c.doFinal (CipherMat);
            return PlainMat;
        }
        catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |InvalidAlgorithmParameterException ex)
        {
            throw new GmException ("AESDecrypt", GmException.GenImpossibleException, Pass, ex);
        }
        catch (IllegalBlockSizeException | BadPaddingException  ex)
        {
            throw new GmException ("Invalid Password or Data.", GmException.CryptInvalidPassword, Pass, ex);
        }
    }
    
    /**
     * Κρυπτογραφεί ένα αρχείο και γράφει το κρυπτόγραμμα σε ένα νέο αρχείο. Το αρχείο που κρυπτογραφείται δεν τροποποιείται. Το αρχείο
     * διαβάζεται ολόκληρο στην μνήμη και κρυπτογραφείται συνολικά. Η κρυπτογράφηση πραγματοποιείται με την χρήση της {@link #EncryptBuf}.
     * Στην αρχή του αρχείου καταγράφεται header με τον κωδικό της βιβλιοθήκης και το IV. Για λόγους συμβατότητας με την 
     * {@link #EncryptFile} και λόγο του XOR, το αρχείο θα πρέπει να είναι μικρότερο από 500ΚΒ (δεν ελέγχεται).
     * @param Sou Το όνομα του πηγαίου αρχείου.
     * @param Dest Το όνομα του αρχείου που θα δημιουργηθεί (κρυπτόγραμμα).
     * @param Pass1 Το συνθηματικό για τον AES.
     * @param Pass2 Το συνθηματικό για τον XOR.
     * @throws GmException Σε περίπτωση που δημιουργηθούν προβλήματα στο διάβασμα ή το γράψιμο των αρχείων και σε περίπτωση που
     * δημιουργηθούν προβλήματα στις μεθόδους που καλούνται.
     */
    public static void EncryptSmallFile (String Sou, String Dest, String Pass1, String Pass2) throws GmException
    {
        byte[] UnEncrypted;
        byte[] XORed;
        File S = new File (Sou);
        if (!S.canRead ())
            throw new GmException ("Cannot Open Unencrypted File", GmException.CryptCannotReadFile, Sou);
        File D = new File (Dest);
        try
        {
            if (!D.createNewFile ())
                throw new GmException ("Error Creating Encrypted File", GmException.CryptCannotCreateFile, Dest);
        }
        catch (IOException ex)
        {
            throw new GmException ("Error Creating Encrypted File", GmException.CryptCannotWriteFile, Dest);
        }
        try
        {
            UnEncrypted = Files.readAllBytes (S.toPath ());
        }
        catch (IOException ex)
        {
            throw new GmException ("Cannot Read Unencrypted File", GmException.CryptCannotReadFile, Sou);
        }
        XORed = EncryptBuf (UnEncrypted, Pass1, Pass2);
        try
        {     
            FileOutputStream fos = new FileOutputStream (D);
            fos.write (XORed);
            fos.close ();
        }
        catch (IOException ex)
        {
            throw new GmException ("Cannot Write Encrypted File", GmException.CryptCannotWriteFile, Dest);
        }
    }
    
    /**
     * Αποκρυπτογραφεί ένα αρχείο το οποίο δημιουργήθηκε από την {@link #EncryptSmallFile }. Δημιουργεί νέο αρχείο χωρίς να τροποποιεί το 
     * αρχικό (κρυπτόγραμμα). Αποκρυπτογραφεί και την προηγούμενη έκδοση της βιβλιοθήκης με το σταθερό Iv. Η αποκρυπτογράφηση γίνεται με την
     * χρήση της {@link #DecryptBuf}.
     * @param Sou Το κρυπτογραφημένο αρχείο.
     * @param Dest Το αποκρυπτογραφημένο αρχείο.
     * @param Pass1 Το συνθηματικό για τον AES.
     * @param Pass2 Το συνθηματικό για τον XOR.
     * @throws GmException Σε περίπτωση προβλήματος στο διάβασμα και το γράψιμο των αρχείων και σε περίπτωση αποτυχίας αποκρυπτογράφησης
     * λόγω λάθους συνθηματικού ή καταστραμένων δεδομένων.
     */
    public static void DecryptSmallFile (String Sou, String Dest, String Pass1, String Pass2) throws GmException
    {
        byte[] UnEncrypted = null;
        byte[] XORed = null;
        File S = new File (Sou);
        if (!S.canRead ())
            throw new GmException ("Cannot Open Encrypted File", GmException.CryptCannotReadFile, Sou);
        File D = new File (Dest);
        try
        {
            if (!D.createNewFile ())
                throw new GmException ("Error Creating Derypted File", GmException.CryptCannotWriteFile, Dest);
        }
        catch (IOException ex)
        {
            throw new GmException ("Error Creating Decrypted File", GmException.CryptCannotWriteFile, Dest);
        }
        try
        {
            XORed = Files.readAllBytes (S.toPath ());
        }
        catch (IOException ex)
        {
            throw new GmException ("Cannot Read Unencrypted File", GmException.CryptCannotReadFile, Sou);
        }
        try
        {
            UnEncrypted = DecryptBuf (XORed, Pass1, Pass2);
        }
        catch (GmException e)
        {
            if (e.getErrorCode () == GmException.CryptInvalidPassword)
                throw new GmException (e.getMessage (), GmException.CryptInvalidPassword, Sou);
        }
        try
        {
            
            FileOutputStream fos = new FileOutputStream (D);
            fos.write (UnEncrypted);
            fos.close ();
        }
        catch (IOException ex)
        {
            throw new GmException ("Cannot Write Decrypted File", GmException.CryptCannotWriteFile, Dest);
        }
    }
    
    /**
     * Σβήνει ένα αρχείο αφού τροποποιήσει αρκετές φορές τα περιεχόμενά του ώστε να μην μπορούν να ανακτηθούν σε περίπτωση αναίρεσης της
     * διαγραφής με ειδικά εργαλεία. Το μέγεθος του αρχείου πρέπει να είναι τέτοιο που να επιτρέπει την δημιουργία ενός buffer στη
     * μνήμη ίδιου μεγέθους.
     * @param Fn Το όνομα του αρχείου που θα διαγραφεί.
     * @throws GmException Σε περίπτωση που το αρχείο δεν υπάρχει ή είναι αδύνατη η τροποποίησή του.
     */
    public static void DestroySmallFile (String Fn) throws GmException
    {
        try
        {
            File F = new File (Fn);
            if (!F.exists ())
                throw new GmException ("File not Found", GmException.CryptCannotReadFile, Fn);
            RandomAccessFile Rf = new RandomAccessFile (Fn, "rws");
            Random Rnd = new Random ();
            byte[] Mat = new byte[(int) Rf.length ()];
            for (int i = 0; i < Mat.length; i++)
                Mat[i] = (byte) 0;
            Rf.seek (0);
            Rf.write (Mat);
            for (int i = 0; i < Mat.length; i++)
                Mat[i] = (byte) -1;
            Rf.seek (0);
            Rf.write (Mat);
            for (int i = 0; i < 5; i++)
            {
                Rnd.nextBytes (Mat);
                Rf.seek (0);
                Rf.write (Mat);
            }
            Rf.close ();
            F.delete ();            
        }
        catch (IOException e)
        {
            throw new GmException ("Cannot Destroy File", GmException.CryptCannotWriteFile, Fn);
        }

    }
    
    /**
     * Κρυπτογραφεί ένα πίνακα από bytes. Η κρυπτογράφηση πραγματοποιείται σε δύο στάδια. Πρώτα εφαρμόζεται στα δεδομένα ο αλγόριθμος AES
     * με την χρήση της {@link #AESEncrypt } και την συνέχεια ο αλγόριθμος XOR με την χρήση της {@link #XORCrypt}. Για κάθε ένα από τα
     * στάδια χρησιμοποιείται διαφορετικό συνθηματικό. Στο πρώτο στάδιο στο τέλος προστίθενται το string προσδιορισμού της βιβλιοθήκης και
     * το IvSpec της κρυπτογράφησης.
     * @param Buf Τα δεδομένα που θα κρυπτογραφηθούν.
     * @param Pass1 Το συνθηματικό για τoν AES.
     * @param Pass2 Το συνθηματικό για τον XOR.
     * @return Τα κρυπτογραφημένα δεδομένα.
     * @throws GmException Σε περίπτωση που δημιουργηθεί λάθος από τις βιβλιοθήκες.
     */
    public static byte[] EncryptBuf (byte[] Buf, String Pass1, String Pass2) throws GmException
    {
        byte[] XORed;
        byte[] Encrypted;    
        byte[] tmp1;
        byte[] tmp2;
        byte[] FullBuff;
        byte[] Header;
        try
        {
            tmp1 = "Fantom01.5".getBytes ("UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new GmException ("EncryptBuf", GmException.GenImpossibleException, "charset UTF-8");
        }
        tmp2 = CreateRandomIV ();
        Encrypted = AESEncrypt (Buf, Pass1, tmp2);
        XORed = XORCrypt (Encrypted, Pass2);
        
        Header = new byte[26];
        System.arraycopy (tmp1, 0, Header, 0, 10);
        System.arraycopy (tmp2, 0, Header, 10, 16);
        Header = XORCrypt (Header, Pass2);
        
        FullBuff = new byte[26 + XORed.length];
        System.arraycopy (Header, 0, FullBuff, 0, Header.length);
        System.arraycopy (XORed, 0, FullBuff, 26, Encrypted.length);
        
        return FullBuff;
    }
    
    /**
     * Αποκρυπτογραφεί ένα πίνακα από bytes ο οποίος δημιουργήθηκε από την {@link #EncryptBuf }. 
     * @param Buf Τα δεδομένα που θα αποκρυπτογραφηθούν.
     * @param Pass1 Το συνθηματικό για τον AES.
     * @param Pass2 Το συνθηματικό για τον XOR.
     * @return Τα αποκρυπτογραφημένα δεδομένα.
     * @throws GmException Σε περίπτωση αποτυχίας αποκρυπτογράφησης λόγω λάθους συνθηματικού ή καταστραμένων δεδομένων.
     */
    public static byte[] DecryptBuf (byte[] Buf, String Pass1, String Pass2) throws GmException
    {
        byte[] UnEncrypted;
        byte[] tmp1;
        byte[] tmp2;
        byte[] Header;
        byte[] Encrypted;
        
        
        Header = Arrays.copyOfRange (Buf, 0, 26);
        Header = XORCrypt (Header, Pass2);
        tmp1 = Arrays.copyOfRange (Header, 0, 10);
        if (!new String (tmp1).equals ("Fantom01.5"))
        {
            Encrypted = XORCrypt (Buf, Pass2);
            UnEncrypted = AESDecrypt (Encrypted, Pass1, null);
        }
        else
        {
            tmp2 = Arrays.copyOfRange (Header, 10, 26);
            tmp1 = Arrays.copyOfRange (Buf, 26, Buf.length);
            Encrypted = XORCrypt (tmp1, Pass2);
            UnEncrypted = AESDecrypt (Encrypted, Pass1, tmp2);
        }
        return UnEncrypted;
    }
    
    
    /**
     * Κρυπτογραφεί ένα αρχείο και γράφει το κρυπτόγραμμα σε ένα νέο αρχείο. Το αρχείο που κρυπτογραφείται δεν τροποποιείται. Το αρχείο
     * διαβάζεται τμηματικά και μπορεί να έχει οποιοδήποτε μέγεθος. Η κρυπτογράφηση του κάθε τμήματος πραγματοποιείται σε δύο στάδια. Πρώτα
     * εφαρμόζεται στα δεδομένα ο αλγόριθμος AES και την συνέχεια ο αλγόριθμος XOR με την χρήση της {@link #XORCrypt}. Για κάθε ένα από τα
     * στάδια χρησιμοποιείται διαφορετικό συνθηματικό.
     * @param Sou Το όνομα του προς κρυπτογράφηση αρχείου.
     * @param Dest Το όνομα του κρυπτογράμματος.
     * @param Pass1 Το συνθηματικό (κλειδί) για τον AES. 
     * @param Pass2 Το συνθηματικό (κλειδί) για τον XOR.
     * @throws GmException Σε περίπτωση που δεν μπορεί να διαβαστεί το πηγαίο αρχείο ή να γραφεί το κρυπτόγραμμα και σε περίπτωση που 
     * δημιουργηθούν εξαιρέσεις στις μεθόδους που καλούνται.
     */
    public static void EncryptFile (String Sou, String Dest, String Pass1, String Pass2) throws GmException
    {
        byte[] UnEncrypted;
        byte[] XORed;
        byte[] Encrypted;
        byte[] Header;
        byte[] tmp1;
        //byte[] Iv = {45, 26, 11, 120, 32, 0, 1, 12, 1, 65, 9, 10, 34, 45, 2, 2};
        byte[] Iv = CreateRandomIV ();
        IvParameterSpec IvSpec = new IvParameterSpec (Iv);
        byte[] Key;
        Cipher c;
        int Br;
        try
        {
            Key = Password2Key (Pass1);
            c = Cipher.getInstance ("AES/CBC/PKCS5Padding");
            SecretKeySpec k = new SecretKeySpec (Key, "AES");
            c.init (Cipher.ENCRYPT_MODE, k, IvSpec);          
        }
        catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |InvalidAlgorithmParameterException e)
        {
            throw new GmException ("File Encrypt", GmException.GenImpossibleException, Pass1, e);
        }
     
        UnEncrypted = new byte[524288];
        
        File S = new File (Sou);
        if (!S.canRead ())
            throw new GmException ("Cannot Open Unencrypted File", GmException.CryptCannotReadFile, Sou);
        File D = new File (Dest);
        try
        {
            if (!D.createNewFile ())
                throw new GmException ("Error Creating Encrypted File", GmException.CryptCannotCreateFile, Dest);
        }
        catch (IOException ex)
        {
            throw new GmException ("Error Creating Encrypted File", GmException.CryptCannotWriteFile, Dest);
        }
        try (FileInputStream Fis = new FileInputStream (S);
                FileOutputStream Fos = new FileOutputStream (D))
        {
            Header = new byte[26];
            tmp1 = "Fantom01.5".getBytes ("UTF-8");
            System.arraycopy (tmp1, 0, Header, 0, 10);
            System.arraycopy (Iv, 0, Header, 10, 16);
            XORed = XORCrypt (Header, Pass2);
            Fos.write (XORed);
            while ((Br = Fis.read (UnEncrypted)) == 524288)
            {
                Encrypted = c.update (UnEncrypted);
                XORed = XORCrypt (Encrypted, Pass2);
                Fos.write (XORed);
            }
            if (Br > 0)
                Encrypted = c.doFinal (UnEncrypted, 0, Br);
            else
                Encrypted = c.doFinal ();
            XORed = XORCrypt (Encrypted, Pass2);
            Fos.write (XORed);
        }
        catch (IOException | IllegalBlockSizeException | BadPaddingException e)
        {
            throw new GmException ("AESEncrypt", GmException.GenImpossibleException, Pass1, e);
        }
    } 
    
    
    /**
     * Αποκρυπτογραφεί ένα αρχείο το οποίο δημιουργήθηκε με την {@link #EncryptFile }. Δημιουργεί νέο αρχείο χωρίς να τροποποιεί το αρχικό 
     * (κρυπτόγραμμα).
     * @param Sou Το αρχείο που θα αποκρυπτογραφηθεί. 
     * @param Dest Το αποκρυπτογραφημένο αρχείο που θα δημιουργηθεί.
     * @param Pass1 Το συνθηματικό (κλειδί) για τον AES.
     * @param Pass2 Το συνθηματικό για τον XOR.
     * @throws GmException Σε περίπτωση που δεν μπορεί να γίνει ανάγνωση / εγγραφή των αρχείων και σε περίπτωση που τα συνθηματικά είναι 
     * λανθασμένα ή το κρυπτογραφημένο αρχείο είναι κατεστραμένο.
     */
    public static void DecryptFile (String Sou, String Dest, String Pass1, String Pass2) throws GmException
    {
        byte[] UnEncrypted;
        byte[] XORed;
        byte[] Encrypted;
        byte[] Iv; 
        byte[] Key;
        Cipher c;
        int Br;
        
        File S = new File (Sou);
        if (!S.canRead ())
            throw new GmException ("Cannot Open Encrypted File", GmException.CryptCannotReadFile, Sou);
        File D = new File (Dest);
        try
        {
            if (!D.createNewFile ())
                throw new GmException ("Error Creating UnEncrypted File", GmException.CryptCannotCreateFile, Dest);
        }
        catch (IOException ex)
        {
            throw new GmException ("Error Creating UnEncrypted File", GmException.CryptCannotWriteFile, Dest);
        }
        XORed = new byte[524288];
        try (BufferedInputStream Fis = new BufferedInputStream (new FileInputStream (S));
                FileOutputStream Fos = new FileOutputStream (D))
        {
            Iv = GetIVFromFile (Fis, Pass2);
            IvParameterSpec IvSpec = new IvParameterSpec (Iv);
            Key = Password2Key (Pass1);
            c = Cipher.getInstance ("AES/CBC/PKCS5Padding");
            SecretKeySpec k = new SecretKeySpec (Key, "AES");
            c.init (Cipher.DECRYPT_MODE, k, IvSpec);  
            while ((Br = Fis.read (XORed)) == 524288)
            {
                Encrypted = XORCrypt (XORed, Pass2);
                UnEncrypted = c.update (Encrypted);
                Fos.write (UnEncrypted);
            }
            if (Br == 0)
                UnEncrypted = c.doFinal ();
            else
            {
                Encrypted = XORCrypt (XORed, Pass2);
                UnEncrypted = c.doFinal (Encrypted, 0, Br);
            }
            
            if (UnEncrypted != null && UnEncrypted.length > 0)
            Fos.write (UnEncrypted);            
        }
        catch (IOException e)
        {
            throw new GmException ("Decrypt File IO Error", GmException.CryptCannotWriteFile, Dest, e);
        }
        catch (IllegalBlockSizeException | BadPaddingException e)
        {
            throw new GmException ("Invalid Password or Data", GmException.CryptInvalidPassword, Dest, e);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex)
        {
            ex.printStackTrace ();
            throw new GmException ("Decrypt File", GmException.GenImpossibleException, Pass1, ex);
        }
        
    } 
    
    /**
     * Σβήνει ένα αρχείο αφού αντικαταστήσει τα περιεχόμενα του αρκετές φορές με διάφορες τιμές ώστε να μην μπορεί να διαβαστεί αν ανακτηθεί
     * με ειδικά εργαλεία.Το μέγεθος του αρχείου μπορεί να μεγαλώσει λίγο (σε πολλαπλάσιο του 512K) πριν διαγραφεί οριστικά. Οι εγγραφές που
     * γίνονται σε κάθε byte είναι μόλις 7 προκειμένου να μην δημιουργούνται προβλήματα σε SSD δίσκους και ταμπλέτες.
     * @param Fn Το όνομα του αρχείου που θα καταστραφεί. 
     * @throws GmException Σε περίπτωση που το αρχείο δεν υπάρχει ή δεν μπορεί να γίνει τροποποίηση των περιεχομένων του.
     */
    public static void DestroyFile (String Fn) throws GmException
    {
        final int BlockS = 512 * 1024;
        try
        {
            File F = new File (Fn);
            if (!F.exists ())
                throw new GmException ("File not Found", GmException.CryptCannotReadFile, Fn);
            RandomAccessFile Rf = new RandomAccessFile (Fn, "rws");
            Random Rnd = new Random ();
            byte[] Mat = new byte[BlockS];
            long Fs = Rf.length ();
            int Blks = (int) (Fs / BlockS + 1);
            for (int B = 0; B < Blks; B++)
            {
                for (int i = 0; i < BlockS; i++)
                    Mat[i] = (byte) 0;
                Rf.seek (B * BlockS);
                Rf.write (Mat);
                for (int i = 0; i < BlockS; i++)
                    Mat[i] = (byte) -1;
                Rf.seek (B * BlockS);
                Rf.write (Mat);
                for (int i = 0; i < 5; i++)
                {
                    Rnd.nextBytes (Mat);
                    Rf.seek (B * BlockS);
                    Rf.write (Mat);
                }
            }
            Rf.close ();
            F.delete ();            
        }
        catch (IOException e)
        {
            throw new GmException ("Cannot Destroy File", GmException.CryptCannotWriteFile, Fn);
        }
    }
  
    /**
     * Δημιουργεί ένα τυχαίο Iv πίνακα για συμμετρική κρυπτογράφηση. Ο πίνακας θα αποτελείται από 16 bytes.
     * @return Ο Iv πίνακας.
     */
    public static byte[] CreateRandomIV ()
    {
        byte[] RIv = new byte[16];
        Random Ra = new Random ();
        Ra.nextBytes (RIv);
        return RIv;
    }
    
    /**
     * Διαβάζει τα πρώτα 26 bytes ενός αρχείου κρυπτογραφημένου με την {@link #EncryptFile} (το οποίο είναι ανοιχτό και στην αρχή του) και
     * επιστρέφει το Iv του αφού εξετάσει την έκδοση της βιβλιοθήκης με την οποία φτιάχθηκε.Αν ο header δεν περιέχει τη σωστή έκδοση
     * της βιβλιοθήκης επιστρέφεται το παλαιο Iv και ο δείκτης τυο αρχείου επιστρέφει στην αρχή του. 
     * @param S Το αρχείο που θα διαβαστεί 
     * @param Pass Το Password με το οποίο αποκρυπτογραφείται ο Header (Xor)
     * @return To Iv με το οποίο θα γίνει η αποκρυπτογράφηση.
     */
    public static byte[] GetIVFromFile (BufferedInputStream S, String Pass)
    {
        byte[] IvOld = {45, 26, 11, 120, 32, 0, 1, 12, 1, 65, 9, 10, 34, 45, 2, 2};
        byte[] Iv;
        byte[] Header = new byte[26];
        byte[] Lib = new byte[10];
        try
        {
            S.mark (100);
            S.read (Header);
            Header = XORCrypt (Header, Pass);
            System.arraycopy (Header, 0, Lib, 0, 10);
            if (!new String (Lib).equals ("Fantom01.5"))
            {
                S.reset ();
                Iv = IvOld;
            }
            else
            {
                Iv = new byte[16];
                System.arraycopy (Header, 10, Iv, 0, 16);
            }
        }
        catch (IOException | GmException e)
        {
            Iv = null;
        }
        return Iv;
    }
    
}


