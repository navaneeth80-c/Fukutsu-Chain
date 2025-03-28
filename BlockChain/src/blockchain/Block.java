package blockchain;
import java.util.Date;
import java.util.*;
public class Block {
	
	public String hash;
	public String previousHash;
	private String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timeStamp;
	private int nonce;
	
	
	//block constructor
	
	public Block(String previousHash)
	{
		
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = this.caclculateHash();
	}
	
	public String caclculateHash() 
	{
		String calculatedHash = StringUtil.applySHA256( this.previousHash + Long.toString(this.timeStamp)+Integer.toString(this.nonce)+this.merkleRoot);
		return calculatedHash;
	}
	
	
	public void mineBlock(int difficulty)
	{
		//String target = new String(new char[difficulty]).replace('\0', '0');
		this.merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty);
		
		while(!this.hash.substring(0,difficulty).equals(target))
		{
			this.nonce++;
			this.hash = this.caclculateHash()	;
		}
		System.out.println("block is mined whith the hash: "+this.hash);
	}
	public boolean addTransaction(Transaction transaction)
	{
		if(transaction == null)
		{
			return false;
		}
		if((previousHash != "0"))
		{
			if((transaction.processTransaction() != true))
			{
				System.out.println(" transaction faild to process!!");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

}
