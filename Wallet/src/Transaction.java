import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey receiver;
	
	public float value;
	public byte[] signature;
	
	public ArrayList<Transaction_input> input = new ArrayList<Transaction_input>();
	public ArrayList<Transaction_output> output = new ArrayList<Transaction_output>();
	
	public static int sequence = 0;
	
	public Transaction(PublicKey sender , PublicKey receiver , float value , ArrayList<Transaction_input> input)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.value = value;
		this.input = input;
	}
	
	public String calculateHash() {
		sequence++;
		
		return Sha256.apply_sha256(StringUtil.get_string_from_key(sender)+
				                   StringUtil.get_string_from_key(receiver)+
				                      Float.toString(value)+
				                      sequence
				                      );
		
	}
	
	
	public void generate_signature(PrivateKey private_key)
	{
		String data = StringUtil.get_string_from_key(sender) + 
				      StringUtil.get_string_from_key(receiver) + 
				      Float.toString(value);
		
		signature = StringUtil.apply_ECDSA_Sign(private_key , data);
	}
	
	public boolean verify_signature()
	{
		String data = StringUtil.get_string_from_key(sender)+
				      StringUtil.get_string_from_key(receiver) + 
				      Float.toString(value);
		
		return StringUtil.verify_ECDSA_Sign(sender , data , signature);
	}
	
	
	public boolean process_transaction()
	{
		
		if(verify_signature()==false)
		{
			System.out.println(" Transaction Signature failed to verify ");
			return false;
			
		}
		
		for(Transaction_input i : input )
		{
			i.unspent_txn_op = Blockchain.unspent_txn_ops.get(i.txn_output_id);
		}
		
		if(get_inputs_value() < Blockchain.minimum_transaction)
		{
			System.out.println(" Transaction Inputs too small: " + get_inputs_value());
			
			return false;
		}
		
		float left_over = get_inputs_value() - value;
		
		String transaction_id = calculateHash();
		
		output.add(new Transaction_output(this.receiver , value , transaction_id));
		output.add(new Transaction_output(this.sender , left_over , transaction_id ));
		
		for(Transaction_output o : output)
		{
			Blockchain.unspent_txn_ops.put(o.id , o);
		}
		
		for(Transaction_input i : input)
		{
			if(i.unspent_txn_op == null)
				continue;
			
			Blockchain.unspent_txn_ops.remove(i.unspent_txn_op.id);
		}
		
		return true;
		
	}
	
	
	public float get_inputs_value()
	{
		float total = 0;
		
		for(Transaction_input i : input)
		{
			if(i.unspent_txn_op == null)
				continue;
			
			total += i.unspent_txn_op.value;
		}
		
		return total;
	}
	
	public float get_outputs_value()
	{
		float total =0;
		
		for(Transaction_output o : output)
		{
			total += o.value;
		}
		
		return total;
		
		
	}
	
	
	
	
}
