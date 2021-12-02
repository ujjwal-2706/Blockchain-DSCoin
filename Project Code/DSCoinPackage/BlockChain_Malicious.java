package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    if(!tB.dgst.substring(0,4).equals("0000"))
    {
      return false;
    }
    else
    {
      CRF obj= new CRF(64);
      if(tB.previous != null)
      {
        if(!tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)))
        {
          return false;
        }
        else
        {
          MerkleTree mtree = new MerkleTree();
          String summary = mtree.Build(tB.trarray);
          if(!tB.trsummary.equals(summary))
          {
            return false;
          }
          else
          {
            for(int i=0;i < tB.trarray.length;i++)
            {
              if(!tB.checkTransaction(tB.trarray[i]))
              {
                return false;
              }
            }
            return true;
          }
        }
      }
      else
      {
        if(!tB.dgst.equals(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)))
        {
          return false;
        }
        else
        {
          MerkleTree mtree = new MerkleTree();
          String summary = mtree.Build(tB.trarray);
          if(!tB.trsummary.equals(summary))
          {
            return false;
          }
          else
          {
            for(int i=0;i < tB.trarray.length;i++)
            {
              if(!tB.checkTransaction(tB.trarray[i]))
              {
                return false;
              }
            }
            return true;
          }
        }
      }
    }
  }

  public TransactionBlock FindLongestValidChain () {
    int[] path_length = new int[lastBlocksList.length];
    int[] validPath = new int[lastBlocksList.length];
    for(int i=0;i< lastBlocksList.length;i++)
    {
      if(lastBlocksList[i] != null)
      {
        int path = 0;
        int valid = 0;
        TransactionBlock traverse = lastBlocksList[i];
        while(traverse != null)
        {
          if(checkTransactionBlock(traverse))
          {
            path++;
            valid++;
            traverse  = traverse.previous;
          }
          else
          {
            path++;
            valid = 0;
            traverse = traverse.previous;
          }
        }
        path_length[i] = path;
        validPath[i] = valid;
      }
    }
    //Checking for index of the path with maximum valid blocks
    int max=0;
    for(int j=0;j< validPath.length;j++)
    {
      if(lastBlocksList[j] != null)
      {
        if(validPath[max] < validPath[j])
        {
          max = j;
        }
      }
    }
    int steps = 0;
    if(lastBlocksList[max] != null)
    {
      steps = path_length[max] - validPath[max];
    }
    TransactionBlock result= lastBlocksList[max];
    while(steps>0)
    {
      result = result.previous;
      steps--;
    }
    return result;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock last = this.FindLongestValidChain();
    if(last != null)
    {
      CRF obj = new CRF(64);
      // nonce computation
      String nonce_val = "1000000001";
      while(true)
      {
        String s = obj.Fn(last.dgst + "#" + newBlock.trsummary + "#" + nonce_val);
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
      newBlock.dgst = obj.Fn(last.dgst + "#" + newBlock.trsummary + "#" + nonce_val);
      newBlock.previous = last;
      int j = lastBlocksList.length;
      for(int i=0;i< lastBlocksList.length;i++)
      {
        if(last == lastBlocksList[i])
        {
          j= i;
        }
      }
      if(j == lastBlocksList.length)
      {
        int k1 =0 ;
        while(lastBlocksList[k1] != null)
        {
          k1++;
        }
        lastBlocksList[k1] = newBlock;
      }
      else
      {
        lastBlocksList[j] = newBlock;
      }
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
      newBlock.previous = last;
      lastBlocksList[0] = newBlock;
    }
  }
}

