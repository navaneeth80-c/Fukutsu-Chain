package blockchain;
import java.security.*;
import java.util.*;

public class Transaction {
	public String transactionId; // hash of the transaction 
	public PublicKey sender; //sender address 
	public PublicKey reciepient ; 
	float value;
	byte[] signature;  //to prevent any others from spending the fund
			 // TransactionInput
	ArrayList<TransactionInput> inputs = new  ArrayList<>();
	ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	public static int sequence = 0;
	
	
	public Transaction(PublicKey from,PublicKey to,float value,ArrayList<TransactionInput> input)
	{
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = input;
	}
	
	
	
	// this will calculate the transaction hash 
	
	private String calculateHash()
	{
		Transaction.sequence ++;
		
		return StringUtil.applySHA256(StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value));
		
	}
	
	public void generateSignature(PrivateKey privateKey)
	{
		String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value);
		signature = StringUtil.applyECDSASignature(privateKey, data);
	}
	
	public boolean signatureVerification()
	{
		String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value);
		return StringUtil.verifyECDSASignature(sender, data, signature);
	}
	
	public boolean processTransaction()
	{
		if(this.signatureVerification()== false)
		{
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		
		for(TransactionInput i : inputs)
		{
			i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
		}
		
		// checking if transaction is valid;
		if(getInputValue() < BlockChain.minimumTransaction)
		{
			System.out.println("Transaction inputs to small : "+ getInputValue());
			return false;
		}
		float leftOver = getInputValue() - value; // get the value of 
		transactionId = calculateHash();
		outputs.add(new TransactionOutput( this.reciepient,value,transactionId));
		outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));
		for(TransactionOutput o : outputs)
		{
			BlockChain.UTXOs.put(o.id, o);
		}
		
		for(TransactionInput i : inputs)
		{
			if(i.UTXO == null)
			{
				continue;
			}
			BlockChain.UTXOs.remove(i.UTXO.id);
		}
		return true;
	}
	
	
	public float getInputValue()
	{
		float total = (float) 0.0;
		for(TransactionInput i : inputs)
		{
			if(i.UTXO == null)
			{
				continue; // pass
				
			}
			total+= i .UTXO.value;
		}
		

		return total;
	}
	
	public float getOutputValues()
	{
		float total = (float) 0.0 ;
		
			for(TransactionOutput o : outputs)
			{
				total += o.value;
			}
		
		return total;
	}

}




















































