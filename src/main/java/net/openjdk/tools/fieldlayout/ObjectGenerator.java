package net.openjdk.tools.fieldlayout;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.PrintStream;

public class ObjectGenerator implements Opcodes {

    public static class ByteClassLoader extends ClassLoader {
        private byte[] data;

        public ByteClassLoader(ClassLoader parent, byte[] data) {
            super(parent);
            this.data = data;
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            return defineClass(name, data, 0, data.length);
        }

    }

    public static void generateClasses() throws Exception {
        PrintStream ps = System.out;
        for (int i = 0; i < 256*256*4; i++) {
            FieldLayout.analyze(ps, buildClass("Sample" + i, i));
            ps.println();
        }
    }


    public static Class<?> buildClass(String name, int signature) throws Exception {
        ClassWriter cw = new ClassWriter(0);

        cw.visit(49,
                ACC_PUBLIC + ACC_SUPER,
                name,
                null,
                "java/lang/Object",
                null);

        genFields(cw, "boolean", Type.BOOLEAN_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "byte", Type.BYTE_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "short", Type.SHORT_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "char", Type.CHAR_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "int", Type.INT_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "long", Type.LONG_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "float", Type.FLOAT_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "double", Type.DOUBLE_TYPE.getDescriptor(), signature & 3);
        signature >>= 2;

        genFields(cw, "object", Type.getType(Object.class).getDescriptor(), signature & 3);
        signature >>= 2;


        cw.visitSource(name + ".java", null);
        cw.visitEnd();

        return new ByteClassLoader(Thread.currentThread().getContextClassLoader(), cw.toByteArray()).findClass(name);
    }

    private static void genFields(ClassWriter cw, String prefix, String desc, int count) {
        for (int c = 0; c < count; c++) {
            cw.visitField(ACC_PUBLIC, prefix + "Field" + c, desc, "", 0);
        }
    }

}
