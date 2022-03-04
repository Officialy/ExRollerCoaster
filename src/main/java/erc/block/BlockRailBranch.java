package erc.block;

import erc.block.tileEntity.TileEntityRailBranch2;
import erc.block.tileEntity.TileEntityRailNormal;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.network.ERC_MessageRailMiscStC;
import erc.network.ERC_PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class BlockRailBranch extends BlockRailBase {

	public BlockRailBranch(Properties p_49795_) {
		super(p_49795_);
	}

	@Override
	public boolean isSignalSource(BlockState p_60571_) {
		return true;
	}

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isClientSide())
        {
            boolean flag = world.getBestNeighborSignal(pos) != 0; //getDirectSignalTo
            boolean flag2 = block.isSignalSource(state);
            if (flag || flag2)
            {
            	TileEntityRailBranch2 rail = (TileEntityRailBranch2)world.getBlockEntity(pos);
            	boolean tgle = rail.getToggleFlag();
            	
                if (flag && !tgle)
                {
                	rail.changeRail();
                	rail.changeToggleFlag();
                	ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageRailMiscStC(rail));
                	world.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F); //ʉH
                }
                else if(!flag && tgle)
                {
                	rail.changeToggleFlag();
                }
            }
        }
    }

    protected void onTileEntityInitFirst(Level world, LivingEntity player, Wrap_BlockEntityRail rail, int x, int y, int z)
	{
    	TileEntityRailBranch2 railb = (TileEntityRailBranch2) rail;
		Vec3 metadir = ConvertVec3FromDir(world.getBlockState(new BlockPos(x, y, z)).getValue(BlockRailBase.DIRECTION));
		Vec3 BaseDir = new Vec3(
				-Math.sin(Math.toRadians(player.getYRot())) * (metadir.x!=0?0:1),
				Math.sin(Math.toRadians(player.getXRot())) * (metadir.y!=0?0:1),
				Math.cos(Math.toRadians(player.getYRot())) * (metadir.z!=0?0:1));
    	
    	railb.SetBaseRailPosition(x, y, z, BaseDir, metadir, 20f);
    	
    	int saveflag = railb.getNowRailFlag();
    	railb.changeRail(0);
    	for(int i=0; i<2; ++i)
    	{
    		railb.changeRail(i);
			double yaw = ((float)i-0.5) + Math.toRadians(player.getYRot());
			double pit = ((float)i-0.5) - Math.toRadians(player.getXRot());
			
			Vec3 vecDir = new Vec3(
					-Math.sin(yaw) * (metadir.x!=0?0:1),
					Math.sin(pit) * (metadir.y!=0?0:1),
					Math.cos(yaw) * (metadir.z!=0?0:1) );

//			railb.SetNextRailPosition(x+(int)(vecDir.xCoord*10), y+(int)(vecDir.yCoord*10), z+(int)(vecDir.zCoord*10));
			railb.SetNextRailVectors(
					new Vec3(x+ (vecDir.x*10) +0.5, y+ (vecDir.y*10) +0.5, z+ (vecDir.z*10) +0.5),
					vecDir, 
					railb.getRail().BaseRail.vecUp, 
					0f, 0f,
					railb.getRail().BaseRail.Power,
					-1, -1, -1);
    	}
    	railb.changeRail(saveflag);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityRailBranch2(pos, state);
	}

}
