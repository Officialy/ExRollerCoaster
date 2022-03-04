package erc.block;

import erc.network.ERC_MessageConnectRailCtS;
import erc.network.ERC_PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import erc.manager.ERC_CoasterAndRailManager;
import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class BlockRailBase extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty DIRECTION = HorizontalDirectionalBlock.FACING;

    //                                                                      x              y            z               x            y              z
    protected static final VoxelShape FLOOR_AABB_X = Block.box(3.2, 11.2, 3.2, 12.8, 16, 12.8);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(3.2, 11.2, 3.2, 12.8, 16, 12.8);
    protected static final VoxelShape CEILING_AABB_X = Block.box(3.2, 0.0, 3.2, 12.8, 4.8, 12.8);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(3.2, 0.0, 3.2, 12.8, 4.8, 12.8);

    protected static final VoxelShape NORTH_AABB = Block.box(3.2, 3.2, 0.0, 12.8, 12.8, 4.8);
    protected static final VoxelShape SOUTH_AABB = Block.box(3.2, 3.2, 11.2, 12.8, 12.8, 16);
    protected static final VoxelShape EAST_AABB = Block.box(11.2, 3.2, 3.2, 16, 12.8, 12.8);
    protected static final VoxelShape WEST_AABB = Block.box(0.0, 3.2, 3.2, 4.8, 12.8, 12.8);

    public BlockRailBase(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(FACE, AttachFace.FLOOR));
    }

//	public BlockRailBase()
//	{
//		super(Material.GROUND);
//		this.setHardness(0.3F);
//		this.setResistance(2000.0F);
//		this.setLightOpacity(0);
//		this.setLightLevel(3.8F);//0.6
//	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FACE, DIRECTION);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//		Wrap_TileEntityRail WPrevTile = ERC_BlockRailManager.GetPrevTileEntity(world);
//		Wrap_TileEntityRail WNextTile = ERC_BlockRailManager.GetNextTileEntity(world);
        Wrap_BlockEntityRail tlRailTest = (Wrap_BlockEntityRail) newBlockEntity(pos, state);
//		ERC_TileEntityRailTest tlRailTest = (ERC_TileEntityRailTest)world.getBlockEntity(x, y, z);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (world.isClientSide()) {
            if (ERC_CoasterAndRailManager.isPlacedPrevRail()) {
                ERC_MessageConnectRailCtS packet = new ERC_MessageConnectRailCtS(ERC_CoasterAndRailManager.prevX, ERC_CoasterAndRailManager.prevY, ERC_CoasterAndRailManager.prevZ, x, y, z);
                ERC_PacketHandler.INSTANCE.sendToServer(packet);
            }

            ERC_CoasterAndRailManager.SetPrevData(x, y, z);

            if (ERC_CoasterAndRailManager.isPlacedNextRail()) {
                ERC_MessageConnectRailCtS packet = new ERC_MessageConnectRailCtS(x, y, z, ERC_CoasterAndRailManager.nextX, ERC_CoasterAndRailManager.nextY, ERC_CoasterAndRailManager.nextZ);
                ERC_PacketHandler.INSTANCE.sendToServer(packet);

                ERC_CoasterAndRailManager.ResetData();
            }

            return;
        }

        super.setPlacedBy(world, pos, state, placer, stack);
        //tlRailTest.myisInvalid(); // ?

        onTileEntityInitFirst(world, placer, tlRailTest, x, y, z);

        world.setBlockEntity(tlRailTest);
//		tlRailTest.onTileSetToWorld_Init();
        tlRailTest.syncData();

//		ERC_BlockRailManager.SetPrevData(x, y, z);
    }

    protected void onTileEntityInitFirst(Level world, LivingEntity player, Wrap_BlockEntityRail Wrail, int x, int y, int z) {
        TileEntityRailBase rail = Wrail.getRail();
        double yaw = Math.toRadians(player.getYRot());
        double pit = -Math.toRadians(player.getXRot());
        Vec3 metadir = ConvertVec3FromDir(world.getBlockState(new BlockPos(x, y, z)).getValue(DIRECTION));
        Vec3 vecDir = new Vec3(
                -Math.sin(yaw) * (metadir.x != 0 ? 0 : 1),
                Math.sin(pit) * (metadir.y != 0 ? 0 : 1),
                Math.cos(yaw) * (metadir.z != 0 ? 0 : 1));

        rail.SetBaseRailPosition(x, y, z, vecDir, metadir, 15f);
//		rail.SetNextRailPosition(x+(int)(vecDir.xCoord*10), y+(int)(vecDir.yCoord*10), z+(int)(vecDir.zCoord*10));
        rail.SetNextRailVectors(
                //new Vec3(x+(int)(vecDir.x*10)+0.5, y+(int)(vecDir.y*10)+0.5, z+(int)(vecDir.z*10)+0.5), //FT Seemed to cause rails to not be straight when placed
                new Vec3(x + (vecDir.x * 10) + 0.5, y + (vecDir.y * 10) + 0.5, z + (vecDir.z * 10) + 0.5),
                vecDir,
                rail.getRail().BaseRail.vecUp,
                0f, 0f,
                rail.getRail().BaseRail.Power,
                -1, -1, -1);
        rail.CalcRailLength();
        rail.Init();
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
 /*todo       TileEntityRailBase thistl = ((Wrap_BlockEntityRail) (world.getBlockEntity(world))).getRail();
        if (thistl != null) {
            thistl.setBreak(true);
            Wrap_BlockEntityRail prev = thistl.getPrevRailTileEntity();
            if (prev != null) prev.getRail().NextRail.SetPos(-1, -1, -1);
            Wrap_BlockEntityRail next = thistl.getNextRailTileEntity();
            if (next != null) next.getRail().BaseRail.SetPos(-1, -1, -1);
        }*/
        super.destroy(world, pos, state);
    }

//	@Override
//	public void harvestBlock(Level world, Player player, int x, int y, int z, int p_149636_6_) 
//	{	
////		ERC_MessageSaveBreakRailStC packet = new ERC_MessageSaveBreakRailStC(
////				ERC_BlockRailManager.prevX, ERC_BlockRailManager.prevY, ERC_BlockRailManager.prevZ,
////				ERC_BlockRailManager.nextX, ERC_BlockRailManager.nextY, ERC_BlockRailManager.nextZ
////				);
////		ERC_PacketHandler.INSTANCE.sendTo(packet,(ServerPlayer) player);
//		super.harvestBlock(world, player, x, y, z, p_149636_6_);
//	}

    //	public void addCollisionBoxesToList(Level world, int x, int y, int z, AABB aabb, List list, Entity entity)
//    {
//        if (flag || flag1)
//        {
//            this.setBlockBounds(
//            		((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
//    				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
//    				);
//            super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
//        }
//        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
//    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACE)) {
            case FLOOR:
                switch (state.getValue(FACING).getAxis()) {
                    case X:
                        return CEILING_AABB_X;
                    case Z:
                    default:
                        return CEILING_AABB_Z;
                }
            case WALL:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            case CEILING:
            default:
                switch (state.getValue(FACING).getAxis()) {
                    case X:
                        return FLOOR_AABB_X;
                    case Z:
                    default:
                        return FLOOR_AABB_Z;
                }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, pos2);
    }

    protected static Direction getConnectedDirection(BlockState p_53201_) {
        return switch (p_53201_.getValue(FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> p_53201_.getValue(DIRECTION);
        };
    }
    //    @Override
//    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
//        return boxes[state.getValue(META) & 7].move(pos.getX(), pos.getY(), pos.getZ()); //todo check move == offset
//    }

    public static boolean isBlockRail(Block block) {
        return block instanceof BlockRailBase;
    }

    protected Vec3 ConvertVec3FromDir(Direction dir) {
        return switch (dir) {
            case DOWN -> new Vec3(0, -1, 0);
            case UP -> new Vec3(0, 1, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
            case WEST -> new Vec3(-1, 0, 0);
            case EAST -> new Vec3(1, 0, 0);
            default -> new Vec3(0, 0, 0);
        };
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, context.getHorizontalDirection());
            } else {
                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction);
            }

            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

}
