package com.example.licentav4;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class MyHostApduService extends HostApduService {
    private String SEL_COMMAND_POS_RESP ="9090";
    private String SEL_COMMAND_NEG_RESP ="9001";
    private String APDU_COMMAND_POS_RESP ="9000";

    private String APDU_COMMAND_NEG_RESP ="6AA2";


    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

        String hexCommandApdu = byteArrayToHexString(commandApdu);

        if(hexCommandApdu.equals("00A404000FFFEEDDCCBBAAA0A1B0B1C0C1D0D1E000") && GlobalActivity.is_valid_user==1)
        {
            Log.d("HCE", "Received SEL-COMMAND: " + Arrays.toString(commandApdu));
            Log.d("HCE", "Converted SEL-COMMAND: " + hexCommandApdu);
            GlobalActivity.listen.setValue("0");

            return SEL_COMMAND_POS_RESP.getBytes();
        }
        else if(hexCommandApdu.equals("00A404000FFFEEDDCCBBAAA0A1B0B1C0C1D0D1E100") && GlobalActivity.is_valid_user==1)
        {
            GlobalActivity.flag=1;
            GlobalActivity.listen.setValue("1");
            Log.d("test", String.valueOf(GlobalActivity.is_valid_user));
            Log.d("HCE", "Received APDU COMMAND: " + Arrays.toString(commandApdu));
            Log.d("HCE", "Converted APDU COMMAND: " + hexCommandApdu);

            return APDU_COMMAND_POS_RESP.getBytes();
        }
        else
        {
            GlobalActivity.flag=0;
            GlobalActivity.listen.setValue("0");
            return SEL_COMMAND_NEG_RESP.getBytes();
        }
    }


    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
    }

    private String byteArrayToHexString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            stringBuilder.append(String.format("%02X", b)); // Convert each byte to a 2-digit hexadecimal string
        }
        return stringBuilder.toString();
    }



}





