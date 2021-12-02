package DSCoinPackage;
import java.util.*;
import HelperClasses.MerkleTree;
import HelperClasses.TreeNode;
import HelperClasses.CRF;
import HelperClasses.Pair;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    trarray = new Transaction[t.length];
    for(int i=0; i < t.length;i++)
    {
      trarray[i] = t[i];
    }
    previous= null;
    MerkleTree mtree = new MerkleTree();
    trsummary = mtree.Build(t);
    mtree.numdocs = t.length;
    Tree = mtree;
    dgst = null;
  }

  public boolean checkTransaction (Transaction t) {
    TransactionBlock sourceBlock = t.coinsrc_block;
    boolean isvalid = false;
    if(sourceBlock == null)
    {
      return true;
    }
    else
    { 
      for(int i=0;i < sourceBlock.trarray.length;i++)
      {
        if(t.coinID.equals(sourceBlock.trarray[i].coinID))
        {
          if(t.Source.UID.equals(sourceBlock.trarray[i].Destination.UID))
          {
            isvalid = true;
          }
          else
          {
            return false;
          }
        }
      }
      if(!isvalid)
      {
        return false;
      }
      else
      {
        TransactionBlock traverse = this.previous;
        while(traverse != sourceBlock)
        {
          for(int j=0; j < traverse.trarray.length;j++)
          {
            if(t.coinID.equals(traverse.trarray[j].coinID))
            {
              return false;
            }
          }
          traverse = traverse.previous;
        }
        return true;
      }
    }
  }

  // Sibling Path
  public List<Pair<String,String>> siblingPath(Transaction tObj)
  {
    int i=0;
    while(true)
    {
      if(trarray[i]== tObj)
      {
        break;
      }
      else
      {
        i++;
      }
    }
    List<Integer> position = new ArrayList<Integer>();
    TreeNode traverse = Tree.rootnode;
    int depth1=0;
		while(traverse.left != null)
		{
			traverse=traverse.left;
			depth1++;
		}
    int pathfind = i;
		while(position.size() < depth1)
		{
			position.add(pathfind);
			pathfind = pathfind/2;
		}
    traverse = Tree.rootnode;
    for(int j = position.size()-1; j >= 0;j --)
		{
			if(position.get(j) %2 == 1)
			{
				traverse = traverse.right;
			}
			else
			{
				traverse = traverse.left;
			}
		}
    List<Pair<String,String>> coupledpath = new ArrayList<Pair<String,String>>();
		while(traverse.parent != null)
		{
			Pair<String,String> pair = new Pair<String,String>(null,null);
			pair.first = traverse.parent.left.val;
			pair.second = traverse.parent.right.val;
			coupledpath.add(pair);
			traverse = traverse.parent;
		}
		Pair<String,String> pair = new Pair<String,String>(null,null);
		pair.first = traverse.val;
		coupledpath.add(pair);
    return coupledpath;
  }
}
