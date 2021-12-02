package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    if(lastBlock != null)
    {
      CRF obj = new CRF(64);
      // nonce computation
      String nonce_val = "1000000001";
      while(true)
      {
        String s = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + nonce_val);
        if(s.substring(0,4).equals("0000"))
        {
          break;
        }
        else
        {
          nonce_val = Integer.toString(Integer.parseInt(nonce_val) + 1);
        }
      }
      newBlock.nonce = nonce_val;
      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + nonce_val);
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
    }
    else
    {
      CRF obj = new CRF(64);
      // nonce computation
      String nonce_val = "1000000001";
      while(true)
      {
        String s = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + nonce_val);
        if(s.substring(0,4).equals("0000"))
        {
          break;
        }
        else
        {
          nonce_val = Integer.toString(Integer.parseInt(nonce_val) + 1);
        }
      }
      newBlock.nonce = nonce_val;
      newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + nonce_val);
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
    }
  }
}
