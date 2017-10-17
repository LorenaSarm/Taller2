/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taller2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Instancia {

    public static Ascensor newAscensor() {
        try {
            return (Ascensor) newInstancia(Ascensor.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se pudo crear una nueva instancia de la clase", e);
        }
    }

    public static Object newInstancia(Class clase) throws Exception {
        File src = new File("src");
        File f = new File(src, clase.getCanonicalName().replaceAll("\\.", "/")
                + "Proxy.java");

        StringBuilder sb = new StringBuilder();
        sb.append("package " + clase.getPackage().getName() + ";");
        sb.append("public class " + clase.getSimpleName() + "Proxy extends "
                + clase.getSimpleName() + "{");

        for (Method metodo : clase.getDeclaredMethods()) {
            if (metodo.getAnnotations() != null) {
                sb.append(modifierFromString(metodo.getModifiers()) + " "
                        + metodo.getReturnType().getName() + " "
                        + metodo.getName() + "(");
                for (Parameter parametro : metodo.getParameters()) {
                    sb.append(parametro.getType().getName() + " "
                            + parametro.getName());
                }
                sb.append("){");
                if (metodo.getAnnotation(InvocacionMultiple.class) != null) {
                    InvocacionMultiple anotacion = metodo.getAnnotation(
                            InvocacionMultiple.class);
                    for (int i = 0; i < anotacion.vecesAInvocar() - 1; i++) {
                        sb.append("super." + metodo.getName() + "(");
                        for (Parameter parametro : metodo.getParameters()) {
                            sb.append(parametro.getName());
                        }
                        sb.append(");");
                    }
                    sb.append("super." + metodo.getName() + "(");
                    for (Parameter parametro : metodo.getParameters()) {
                        sb.append(parametro.getName());
                    }
                    sb.append(");");
                }
                sb.append("}");

                if (metodo.getAnnotations() != null) {
                    if (metodo.getAnnotation(Log.class) != null) {
                        //Metodo Auxiliar
                        writeInLog(metodo.getName());
                    }
                }
                if (metodo.getAnnotations() != null) {
                    if (metodo.getAnnotation(PostConstructor.class) != null) {
                        System.out.println("Metodo: " + metodo
                                + " instanciado despues de constructor.");
                    }
                }
            }
        }
        sb.append("}");
        FileWriter fw = new FileWriter(f);
        fw.write(sb.toString());
        fw.flush();
        fw.close();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, f.getPath());

        URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[]{src.toURI().toURL()});
        Class<?> cls = Class.forName(clase.getCanonicalName() + "Proxy",
                true, classLoader);
        f.delete();
        new File(src, clase.getCanonicalName().replaceAll("\\.", "/")
                + "Proxy.class").delete();

        return cls.newInstance();
    }

    private static void writeInLog(String metodo) throws IOException {
        File fileLog = new File("log.txt");
        if (fileLog.exists() == false) {
            FileWriter fileLogWrite = new FileWriter(fileLog, false);
        }
        FileWriter fileLogWrite = new FileWriter(fileLog, true);
        Calendar actualDate = Calendar.getInstance();
        fileLogWrite.write( "Metodo invocado: " + metodo +
                            ", en la fecha: "
                            + (String.valueOf(actualDate.get(Calendar.DAY_OF_MONTH))
                            + "/" + String.valueOf(actualDate.get(Calendar.MONTH) + 1)
                            + "/" + String.valueOf(actualDate.get(Calendar.YEAR))
                            + ", a las: "
                            + String.valueOf(actualDate.get(Calendar.HOUR_OF_DAY))
                            + ":" + String.valueOf(actualDate.get(Calendar.MINUTE))
                            + ":" + String.valueOf(actualDate.get(Calendar.SECOND)))
                            + ".\r\n");
        fileLogWrite.flush();
        fileLogWrite.close();
        System.out.println("Direccion del archivo Log: " + fileLog.getAbsolutePath());
    }

    private static String modifierFromString(int m) {
        switch (m) {
            case Modifier.PUBLIC:
                return "public";
            case Modifier.PROTECTED:
                return "protected";
            case Modifier.PRIVATE:
                return "private";
            case Modifier.STATIC:
                return "static";
            case Modifier.FINAL:
                return "final";
            case Modifier.TRANSIENT:
                return "transient";
            case Modifier.VOLATILE:
                return "volatile";
        }
        return null;
    }

}
