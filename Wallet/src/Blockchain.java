import java.security.Security;
import java.util.HashMap;
import org.bouncycastle.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {
	
	public static ArrayList<Block> block_chain = new ArrayList<Block>();
	public static HashMap<String , Transaction_output> unspent_txn_ops = new HashMap<String , Transaction_output>();
	public static int count = 3;
	public static float minimum_transaction = 0.1f;
	public static Wallet wallet_1;
	public static Wallet wallet_2;
	
	public static Transaction first_txn;
	
	public static void main(String[] args)
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		wallet_1 = new Wallet();
		wallet_2 = new Wallet();
		
		Wallet coin_base = new Wallet();
		
		first_txn = new Transaction(coin_base.public_key , wallet_1.public_key , 100f , null );
		first_txn.generate_signature((coin_base.private_key));
		first_txn.transactionId = "0";
		first_txn.output.add(new Transaction_output(first_txn.receiver , first_txn.value , first_txn.transactionId ));
		unspent_txn_ops.put(first_txn.output.get(0).id ,first_txn.output.get(0));
		
		
		System.out.println("Creating and Mining First block ....\n");
		Block first = new Block("0");
		first.add_transaction(first_txn);
		add_block(first);
		
		
		Block block_1 = new Block(first.my_hash);
		
		System.out.println("\nWallet 1's balance is : " + wallet_1.get_balance());
		System.out.println("\nWallet 1 is attempting to send funds (25) to Wallet 2 .. ");
		
		block_1.add_transaction(wallet_1.send_funds(wallet_2.public_key, 25f));
		
		add_block(block_1);
		
		System.out.println("\nWallet 1's balance is :" + wallet_1.get_balance());
		System.out.println("\nWallet 2's balance is :" + wallet_2.get_balance());
		
		
        Block block_2 = new Block(block_1.my_hash);
		
		
		System.out.println("\nWallet 1 is attempting to send more funds (400) than it has .. ");
		
		block_2.add_transaction(wallet_1.send_funds(wallet_2.public_key, 400f));
		
		add_block(block_2);
		
		System.out.println("\nWallet 1's balance is :" + wallet_1.get_balance());
		System.out.println("\nWallet 2's balance is :" + wallet_2.get_balance());
		
		
        Block block_3 = new Block(block_2.my_hash);
		
		
		System.out.println("\nWallet 2 is attempting to send funds (10) to Wallet 1 .. ");
		
		
		block_3.add_transaction(wallet_2.send_funds(wallet_1.public_key, 10f));
		
		add_block(block_3);
		
		System.out.println("\nWallet 1's balance is :" + wallet_1.get_balance());
		System.out.println("\nWallet 2's balance is :" + wallet_2.get_balance());
		
		is_chain_valid();
		
		
	}
	
	public static Boolean is_chain_valid()
	{
		Block current_block; 
		Block previous_block;
		String hash_target = new String(new char[count]).replace('\0', '0');
		HashMap<String,Transaction_output> temp_UTXOs = new HashMap<String,Transaction_output>(); //a temporary working list of unspent transactions at a given block state.
		temp_UTXOs.put(first_txn.output.get(0).id, first_txn.output.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < block_chain.size(); i++) {
			
			current_block = block_chain.get(i);
			previous_block = block_chain.get(i-1);
			//compare registered hash and calculated hash:
			if(!current_block.my_hash.equals(current_block.hash_calculate()) ){
				System.out.println("#Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previous_block.my_hash.equals(current_block.prev_hash) ) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!current_block.my_hash.substring( 0, count).equals(hash_target)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}
			
			//loop thru blockchains transactions:
			Transaction_output temp_output;
			for(int t=0; t <current_block.txns.size(); t++) {
				Transaction current_txn = current_block.txns.get(t);
				
				if(!current_txn.verify_signature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(current_txn.get_inputs_value() != current_txn.get_outputs_value()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(Transaction_input input: current_txn.input) {	
					temp_output = temp_UTXOs.get(input.txn_output_id);
					
					if(temp_output == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.unspent_txn_op.value != temp_output.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					temp_UTXOs.remove(input.txn_output_id);
				}
				
				for(Transaction_output output: current_txn.output) {
					temp_UTXOs.put(output.id, output);
				}
				
				if( current_txn.output.get(0).receiver != current_txn.receiver) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( current_txn.output.get(1).receiver != current_txn.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
	public static void add_block(Block new_block)
	{
		new_block.mine_block(count);
		block_chain.add(new_block);
	}
	

}
