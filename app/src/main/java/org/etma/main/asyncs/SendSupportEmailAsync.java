package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import org.etma.main.events.SupportEmailSentEvent;
import org.etma.main.helpers.MailGunner;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

public class SendSupportEmailAsync extends AsyncTask<String, Void, Integer> {

    protected Context context;
    private String senderEmail = "chirchir.dev@gmail.com";
    private String senderPass = "$_KIPsigei2003#";

    public SendSupportEmailAsync(Context cxt) {
        this.context = cxt;
    }

    @Override
    protected Integer doInBackground(String... params) {

        MailGunner gunner = new MailGunner(context, params[0]);

        Session session = MailGunner.buildGoogleSession(senderEmail, senderPass);

        Message message = null;

        try {
            message = gunner.buildMessageForSupport(session);
        }catch (MessagingException e){
            e.getMessage();
        }catch (IOException e){
            e.getMessage();
        }

        try {
            gunner.addressAndSendMessage(message, params[1]);

        }catch (AddressException e){
            System.out.print(e.getMessage());
            e.printStackTrace();
        }catch (MessagingException e){
            e.printStackTrace();
            System.out.print(e.getMessage());
        }


        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        EventBus.getDefault().post( new SupportEmailSentEvent( true ));
    }
}

