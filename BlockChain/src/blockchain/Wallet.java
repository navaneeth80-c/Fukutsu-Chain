package blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.security.spec.ECGenParameterSpec;
public class Wallet {
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public Wallet()
	{
		this.generateKeyPair();
	}
	
	
	public void generateKeyPair()
	{
		
		
		
			try {
				 // Add Bouncy Castle as a security provider (only needed if not already added globally)
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			    // "ECDSA" -> Specifies that we are using the Elliptic Curve Digital Signature Algorithm
		        // "BC" -> Specifies the Bouncy Castle security provider for cryptographic functions

		        // Step 2: Create a secure random number generator
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				  // "SHA1PRNG" -> A cryptographically secure pseudo-random number generator
		        // SecureRandom ensures that the generated private key is truly random and unpredictable

		        // Step 3: Define elliptic curve parameters
				ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
				 // "prime192v1" -> A specific 192-bit elliptic curve (also known as secp192r1)
		        // Different curves provide different levels of security and performance

		        // Step 4: Initialize the key generator with curve parameters and randomness
		       
				//initialize the key generator and generate the key 
				keyGen.initialize(ecSpec,random); // 256 bytes provides an accep
				 // ecSpec -> Specifies which elliptic curve to use
		        // random -> Provides cryptographic randomness for key generation

		        // Step 5: Generate the key pair (private and public keys)
		      
				KeyPair keyPair = keyGen.generateKeyPair();
				//getting the public key and private key
				privateKey = keyPair.getPrivate();
				publicKey = keyPair.getPublic();
				
		
		
		
				} catch(Exception e)
				{
		
			
					throw new RuntimeException(e);
				}	
	}
	
	public float getBalance()
	{
		float total = (float) 0.0;
		
		for(Map.Entry<String,TransactionOutput> item: BlockChain.UTXOs.entrySet())
		{
			TransactionOutput UTXO = item.getValue();
			
			if(UTXO.isMine(publicKey)) //if output belongs to me means if coins belongs to me 
			{
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}

		
		return total;
	}
	
	
	public Transaction sendFunds(PublicKey _recipient , float value)
	{
		if(getBalance() < value)
		{
			System.out.println("not enough funds for the transactions Transaction Discarded : ");
			return null;
		}
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	    
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet())
		{
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
}


















