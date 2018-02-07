package net.lstewieal.nochoruslimit;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class NCLTransformer implements IClassTransformer
{
    /* Obfuscated Names for BlockChorusFlower Transformation */

    /* net.minecraft.block.BlockChorusFlower */
    private static final String blockChorusFlowerClassNameO = "apj";

    /* placeDeadFlower / func_185605_c */
    private static final String blockChorusFlowerTargetMethodNameO = "c";

    /* method desc of placeDeadFlower / func_185605_c */
    private static final String methodDescO = "(Lamu;Let;)V";
    private static final String methodDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V";

    /* MCP Names for BlockChorusFlower Transformation */
    private static final String blockChorusFlowerClassName = "net.minecraft.block.BlockChorusFlower";

    private static final String blockChorusFlowerTargetMethodName = "placeDeadFlower";

    /* in transform, the bytes will be not be obfuscated if performing on a mod's class */
    private boolean obfuscation;

    @Override
    public byte[] transform(String name, String newName, byte[] bytes) {
        if (name.equals(blockChorusFlowerClassNameO)) {
            obfuscation = true;
            return handleBlockChorusPlant(bytes);
        }
        else if (name.equals(blockChorusFlowerClassName)) {
            obfuscation = false;
            return handleBlockChorusPlant(bytes);
        }
        return bytes;
    }

    private byte[] handleBlockChorusPlant(byte[] bytes) {
        System.out.println("**************** Transform running on BlockChorusPlant, obfuscated: " + obfuscation + " *********************** ");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        /* find method to inject into */
        MethodNode method = findTargetMethod(classNode);

        /* if the method was not found, return the class unchanged */
        if(method == null) return bytes;

        System.out.println("Target method " + getTargetMethodName() + " found!");
        findAndModifyTargetInstructions(method);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        System.out.println("Patching Complete! Writing class bytes now.");
        return writer.toByteArray();
    }

    private MethodNode findTargetMethod(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if (isTargetMethod(method)) {
                return method;
            }
        }
        /* method was not found */
        return null;
    }

    private void findAndModifyTargetInstructions(MethodNode method) {
        for (int index = 0, n = method.instructions.size(); index < n; index++) {
            if (isTargetInstruction(method, index)) {
                System.out.println("Found ICONST_5 instruction at " + index);
                modifyTargetInstruction(method, index);
                return;
            }
        }
    }

    private void modifyTargetInstruction(MethodNode m, int index) {
        /* replacing iconst_5 with iconst_0 */

        m.instructions.remove(m.instructions.get(index));

        InsnList toInject = new InsnList();
        toInject.add(new InsnNode(Opcodes.ICONST_0));

        m.instructions.insertBefore(m.instructions.get(index), toInject);
    }

    private boolean isTargetInstruction(MethodNode m, int index) {
        return m.instructions.get(index).getOpcode() == Opcodes.ICONST_5;
    }

    private boolean isTargetMethod(MethodNode m) {
        return m.name.equals(getTargetMethodName()) && m.desc.equals(getTargetMethodDesc());
    }

    private String getTargetMethodName() {
        return obfuscation ? blockChorusFlowerTargetMethodNameO : blockChorusFlowerTargetMethodName;
    }

    private String getTargetMethodDesc() {
        return obfuscation ? methodDescO : methodDesc;
    }

}

/*  pre patch, java source

    private void placeDeadFlower(World p_185605_1_, BlockPos p_185605_2_) {
        p_185605_1_.setBlockState(p_185605_2_, this.getDefaultState().withProperty(AGE, Integer.valueOf(5)), 2);
        p_185605_1_.playEvent(1034, p_185605_2_, 0);
}
 */

/* pre patch, obfuscated bytecode

   L0
    LINENUMBER 161 L0
    ALOAD 1
    ALOAD 2
    ALOAD 0
    INVOKEVIRTUAL net/minecraft/block/BlockChorusFlower.getDefaultState ()Lnet/minecraft/block/state/IBlockState;
    GETSTATIC net/minecraft/block/BlockChorusFlower.AGE : Lnet/minecraft/block/properties/PropertyInteger;
    ICONST_5
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    INVOKEINTERFACE net/minecraft/block/state/IBlockState.withProperty (Lnet/minecraft/block/properties/IProperty;Ljava/lang/Comparable;)Lnet/minecraft/block/state/IBlockState;
    ICONST_2
    INVOKEVIRTUAL net/minecraft/world/World.setBlockState (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z
    POP
   L1
 */

