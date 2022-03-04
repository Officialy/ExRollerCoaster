package erc.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;

//sideonly server
public class ERC_ManagerCoasterLoad {
	
	//eMapp
	static class ParentConstructConnection{
		ERC_EntityCoaster parent;
		Map<Integer, ERC_EntityCoasterConnector> childrenMap;
		ParentConstructConnection(ERC_EntityCoaster p)
		{
			parent = p;
			childrenMap = new TreeMap<Integer, ERC_EntityCoasterConnector>();
		}
		public void addChildren(ERC_EntityCoasterConnector child, int idx)
		{
			childrenMap.put(idx, child);
		}
	}

	static class ConnectorCoasterCounter{
		ERC_EntityCoasterConnector coaster;
		int counter = 40;
		public ConnectorCoasterCounter(ERC_EntityCoasterConnector c){coaster = c;}
	}
	
	static Map<UUID, ParentConstructConnection> parentMap = new HashMap<UUID, ParentConstructConnection>();
	static Map<Integer, ConnectorCoasterCounter>childMap = new HashMap<Integer, ConnectorCoasterCounter>();
	
	//e[hꂽ炱ɓo^
	public static void registerParentCoaster(ERC_EntityCoaster parent)
	{
		if(parent.connectNum==0)return;
		parentMap.put(parent.getUUID(), new ParentConstructConnection(parent));
		ERC_Logger.info("register manager: parent, num:"+parent.connectNum + " ... parentid:"+ parent.getUUID());
	}

	public static void registerChildCoaster(ERC_EntityCoasterConnector child)
	{
		childMap.put(child.getId(), new ConnectorCoasterCounter(child));
	}

	public static boolean searchParent(int childid, int idx, UUID parentid)
	{
		if(childMap.get(childid)==null)return false;
		ParentConstructConnection parent = parentMap.get(parentid);
		if(parent == null)
		{
			ERC_Logger.info("find parent false");
			ConnectorCoasterCounter ccc = childMap.get(childid);
//			ERC_Logger.info("CoasterManager countdown:"+(ccc.counter-1)+" parentid:"+parentid.toString());
			if(--ccc.counter <= 0)
			{
				ccc.coaster.killCoaster();
				childMap.remove(childid);
			}
			return false;
		}
		else
		{
//			ERC_Logger.info("find parent, parentID:"+parent.parent.getId()+"childID:"+childid+"num:"+parent.parent.connectNum+">"+(parent.parent.connectNum-1));
			parent.childrenMap.put(idx, childMap.get(childid).coaster);
			if(--parent.parent.connectNum<=0)
			{
				parent.parent.clearConnectCoaster();
				for(Integer i : parent.childrenMap.keySet())
				{
					ERC_EntityCoasterConnector c = parent.childrenMap.get(i);
		            parent.parent.connectionCoaster(c);
		        }
				parentMap.remove(parentid);
//				ERC_Logger.info("connect all children");
			}
			childMap.remove(childid);
		}
		return true;
	}
	
	/*
	todo
	public static Entity SearchEntityWithUUID(Level world, UUID uuid)
	{
		@SuppressWarnings("unchecked")
		List<Entity> elist = world.getLoadedEntityList();
		for(Entity e :elist)
		{
			if(e.getUUID().equals(uuid))
				return e;
		}
		return null;
	}*/
}
