package erc.block;

import erc.block.tileEntity.TileEntityNonGravityRail;
import erc.block.tileEntity.TileEntityRailNormal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Created by MOTTY on 2017/09/30.
 */
public class BlockNonGravityRail extends BlockRailBase {
    public BlockNonGravityRail(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityNonGravityRail(pos, state);
    }

}
