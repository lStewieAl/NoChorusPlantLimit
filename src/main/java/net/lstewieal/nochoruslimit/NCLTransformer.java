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
    private static final String blockChorusFlowerJavaClassNameO = "apj";

    /* placeDeadFlower / func_185605_c */
    private static final String blockChorusFlowerTargetMethodNameO = "c";

    /* method desc of placeDeadFlower / func_185605_c */
    private static final String methodDescO = "(Lamu;Let;)V";
    private static final String methodDesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V";

    /* MCP Names for BlockChorusFlower Transformation */
    private static final String blockChorusFlowerClassName = "net.minecraft.block.BlockChorusFlower";
    private static final String blockChorusFlowerJavaClassName = "net/minecraft/block/BlockChorusFlower";

    private static final String blockChorusFlowerTargetMethodName = "placeDeadFlower";


    private boolean obfuscation;

    @Override
    public byte[] transform(String name, String newName, byte[] bytes)
    {
        // System.out.println("transforming: "+name);
        if (name.equals(blockChorusFlowerClassNameO))
        {
            obfuscation = true;
            return handleBlockChorusPlant(bytes);
        }
        else if (name.equals(blockChorusFlowerClassName))
        {
            obfuscation = false;
            return handleBlockChorusPlant(bytes);
        }

        return bytes;
    }

    private byte[] handleBlockChorusPlant(byte[] bytes)
    {
        System.out.println("**************** Transform running on BlockChorusPlant, obfuscated: " + obfuscation + " *********************** ");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        // find method to inject into
        for (MethodNode m : classNode.methods)
        {
            if (m.name.equals(getTargetMethodName()) && m.desc.equals(getTargetMethodDesc()))
            {
                System.out.println("In target method " + getTargetMethodName() + "! Patching!");

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

                for (int index = 0, n = m.instructions.size(); index < n; index++) {
                    if (m.instructions.get(index).getOpcode() == Opcodes.ICONST_5) {
                        System.out.println("Found ICONST_5 Node at " + index);

                        /* construct it using asm, and replace iconst_5 with iconst_0*/
                        m.instructions.remove(m.instructions.get(index));
                        InsnList toInject = new InsnList();
                        toInject.add(new InsnNode(Opcodes.ICONST_0));
                        m.instructions.insertBefore(m.instructions.get(index), toInject);
                        break;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        System.out.println("Patching Complete! Writing class bytes now.");
        return writer.toByteArray();
    }

    private String getTargetMethodName() {
        return obfuscation ? blockChorusFlowerTargetMethodNameO : blockChorusFlowerTargetMethodName;
    }

    private String getTargetMethodDesc()
    {
        return obfuscation ? methodDescO : methodDesc;
    }

}

