package blockchain;
import java.security.*;
import java.util.*;
import java.security.MessageDigest;
import java.util.Base64;

public class StringUtil {
	
	public static String applySHA256(String input)
	{
		try {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(input.getBytes("UTF-8"));
		StringBuffer hexString = new StringBuffer();
		
		for(int i=0;i< hash.length;i++) {
			
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) 
				{
					hexString.append('0');
				}
			hexString.append(hex);
			}
		return hexString.toString();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static String getStringFromKey(Key key)
	{
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
	
	
	// Applies ECDSA Signature and return the result (as bytes)
	public static byte[] applyECDSASignature(PrivateKey privateKey,String input)
	{
		Signature dsa;
		byte[] output = new byte[0];
		
		try
		{
			dsa = Signature.getInstance("ECDSA","BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[]realSignature = dsa.sign();
			output = realSignature;
			
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return output;
	}
	
	public static boolean verifyECDSASignature(PublicKey publicKey,String data,byte[] signature)
	{
		try {
		Signature ecdsaVerify = Signature.getInstance("ECDSA","BC");
		ecdsaVerify.initVerify(publicKey);
		ecdsaVerify.update(data.getBytes());
		return ecdsaVerify.verify(signature);
		} catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions)
	{
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		
		for(Transaction transaction : transactions)
		{
			previousTreeLayer.add(transaction.transactionId);
		}
		
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1)
		{
			treeLayer = new ArrayList<String>();
			for(int i=1;i<previousTreeLayer.size();i++)
			{
				treeLayer.add(applySHA256(previousTreeLayer.get(i-1)+previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
	
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

}





















