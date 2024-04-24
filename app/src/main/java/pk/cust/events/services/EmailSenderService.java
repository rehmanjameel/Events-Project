package pk.cust.events.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import pk.cust.events.utils.App;

public class EmailSenderService {

    private final Context context;
//    private final Bitmap bitmap;
    private final String sendTo;
    private final String username = "eventsapp526@gmail.com";
    private final String appPassword = "xdyt ifkj ykrr uuud";
    private final String to = "malikarj98@gmail.com";

    public EmailSenderService(Context context,
//                              Bitmap bitmap,
                              String sendTo) {
        this.context = context;
//        this.bitmap = bitmap;
        this.sendTo = sendTo;
    }

    public void sendEmail() {
        // Use Executors.newSingleThreadExecutor() to run the task in a background thread
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new EmailSenderRunnable());
//        layout.setVisibility(View.VISIBLE);
//        button.setVisibility(View.VISIBLE);
    }

    private class EmailSenderRunnable implements Runnable {
        @Override
        public void run() {
            try {
                // Sender's properties
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.host", "smtp.gmail.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.setProperty("mail.smtp.quitwait", "false");

                // Create a session with the sender's credentials
                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, appPassword);
                    }
                });

                // Generate a random 6-digit number
                int min = 100000; // Minimum 6-digit number
                int max = 999999; // Maximum 6-digit number

                Random random = new Random();
                int randomNumber = random.nextInt(max - min + 1) + min;

                // Print or use the generated number as needed
                System.out.println("Random 6-digit number: " + randomNumber);

                // Create a default MimeMessage object
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header
                message.setFrom(new InternetAddress(username));

                // Set To: header field of the header
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));

                // Set Subject: header field
                message.setSubject("EventsApp send you otp!");

                // Create the message body part
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(String.valueOf(randomNumber));

                // Create a multipart message
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);

                // Attach the image from drawable resources
//                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.qr);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();

                // Create a data source from the byte array
//                DataSource dataSource = new ByteArrayDataSource(byteArray, "image/png");

                // Create an image attachment
//                MimeBodyPart imageBodyPart = new MimeBodyPart();
//                imageBodyPart.setDataHandler(new DataHandler(dataSource));
//                imageBodyPart.setFileName("image.png");
//                imageBodyPart.setHeader("Content-ID", "<image>");

                // Add the image to the multipart message
//                multipart.addBodyPart(imageBodyPart);

                // Set the content of the message
                message.setContent(multipart);

                // Send the message
                Transport.send(message);

                // Show a toast on the UI thread
                showToast("Otp sent to email " + sendTo);

                App.saveString("email_otp", String.valueOf(randomNumber));
            } catch (MessagingException e) {
                e.printStackTrace();
                // Show a toast on the UI thread
                showToast("Failed to send email.");
            }
        }
    }


    private void showToast(final String message) {
        // Use Handler to show a toast on the UI thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
