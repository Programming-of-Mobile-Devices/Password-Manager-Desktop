
package org.gmele.general.exceptions;

/**
 * Η κλάση GmException είναι η γενική κλάση για τις εξαιρέσεις όλων των έργων "org.gmele". Η κλάση κληρονομεί την κλάση {@link Exception}
 * και ορίζει νέα attributes. Θα μπορεί να κληρονομηθεί από άλλες κλάσεις αν χρειάζεται (π.χ. για να υλοποιηθούν κάποια interfaces με 
 * κωδικούς λαθών), αλλά αυτό δεν θα είναι απαραίτητο. Υλοποιεί το interface {@link GmExceptionConsts}.
 * @author Giorgos Meletiou
 * @version 2.0
 */
public class GmException extends Exception implements GmExceptionConsts
{
	/**
	 * Ο κωδικός λάθος του Exception. Πρέπει να πάρει τιμές από αυτές που ορίζονται στα σχετικά interfaces.
	 */
	private int ErrorCode;
    
	/**
	 * Δεδομένα σχετικά με το πρόβλημα που δημιουργήθηκε. Μπορεί να περιέχει και προγραμματιστικές πληροφορίες, π.χ. τις τιμές κάποιων
     * μεταβλητών κλπ.
	 */
	private String Data;
	
    /**
	 * Το Exception του συστήματος που προκάλεσε τη δημιουργία του συγκεκριμένου Exception (αν υπάρχει τέτοιο).
	 */
	private Exception SysException;
	
	/**
	 * Ο κατασκευαστής του Exception και ο μοναδικός τρόπος να οριστούν τιμές στα πεδία του (δεν υπάρχουν setters).
	 * @param message Το μήνυμα για τον κατασκευαστή της κλάσης {@link Exception} που κληρονομείται. 
	 * @param errCode Ο κωδικός σφάλματος. Οι κωδικοί πρέπει να πέρνουν τιμές από τo interface {@link GmExceptionConsts} ή από αντίστοιχο.
	 * @param data Τα δεδομένα που αφορούν το σφάλμα σε String. 
	 * @param sysExc Αν το exception δημιουργείται για να ενημερώσει για κάποιο άλλο exception που δημιουργήθηκε από το σύστημα.
	 * (π.χ. ένα SQLException κατά την εκτέλεση εντολών σχετικών με τη βάση δεδομένων) εδώ καταχωρείται το αρχικό exception. Αν δεν υπάρχει 
	 * τέτοια περίπτωση η παράμετρος έχει τιμή null.
	 */
	public GmException (String message, int errCode, String data, Exception sysExc)
	{
		super (message);
		ErrorCode = errCode;
		Data = data;
		SysException = sysExc; 
	}
	
	/**
	 * Βοηθητικός κατασκευαστής για την δημιουργία αντικειμένου χωρίς την παράμετρο SysException. Καλεί τον βασικό κατασκευαστή περνώντας
	 * του null στην σχετική παράμετρο.
	 * @param message Το μήνυμα για τον κατασκευαστή της κλάσης Exception που κληρονομείται.
	 * @param errCode Ο κωδικός σφάλματος. Οι κωδικοί πρέπει να πέρνουν τιμές από τo interface {@link GmExceptionConsts} ή από αντίστοιχο.
	 * @param data Τα δεδομένα που αφορούν το σφάλμα σε String.
	 */
	public GmException (String message, int errCode, String data)
	{
		this(message, errCode, data, (Exception) null);
	}

	/**
	 * Επιστρέφει τον κωδικό λάθους που έχει καταγραφεί στο exception.
	 * @return O κωδικός λάθους.
	 */
	public int getErrorCode ()
	{
		return ErrorCode;
	}

	/**
	 * Επιστρέφει τα δεδομένα που αφορούν το σφάλμα που δημιουργήθηκε. 
	 * @return Η αναλυτική περιγραφή.
	 */
	public String getData ()
	{
		return Data;
	}

	/**
	 * Επιστρέφει το Exception Συστήματος που δημιούργησε το τρέχων exception ή null αν δεν έχει οριστεί τέτοιο. 
	 * @return Το System Exception.
	 */
	public Exception getSysException ()
	{
		return SysException;
	}
    
}
