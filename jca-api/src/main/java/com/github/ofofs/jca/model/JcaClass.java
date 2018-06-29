package com.github.ofofs.jca.model;

import com.github.ofofs.jca.constants.JcaConstants;
import com.github.ofofs.jca.util.Sequence;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class JcaClass extends JcaCommon {

    /**
     * 类的标识
     */
    private Symbol.ClassSymbol classSym;

    /**
     * 类的声明
     */
    private JCTree.JCClassDecl classDecl;

    public JcaClass(Symbol.ClassSymbol classSym) {
        this.classSym = classSym;
        classDecl = (JCTree.JCClassDecl) trees.getTree(classSym);
    }

    /**
     * 插入一个字段
     *
     * @param jcaField 字段
     * @return 返回当前类
     */
    public JcaClass insert(JcaField jcaField) {
        ListBuffer<JCTree> statements = new ListBuffer<>();

        if (existsField(jcaField.getFieldName())) {
            return this;
        }

        importPackage(this, jcaField.getTypeClass());
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(jcaField.getModifiers()), names.fromString(jcaField.getFieldName()), getType(jcaField.getTypeClass()), jcaField.getValue().getExpression()));
        for (JCTree jcTree : classDecl.defs) {
            statements.append(jcTree);
        }
        classDecl.defs = statements.toList();
        return this;
    }

    /**
     * 获取当前类
     *
     * @return 返回当前类
     */
    public Symbol.ClassSymbol getClassSym() {
        return classSym;
    }

    /**
     * 获取类的全路径
     *
     * @return 返回类的全路径
     */
    public String getFullName() {
        return classSym.fullname.toString();
    }

    /**
     * 获取类的包名
     *
     * @return 返回类的包名
     */
    public String getPackageName() {
        return classSym.owner.toString();
    }

    /**
     * 获取类名
     *
     * @return 返回类名
     */
    public String getClassName() {
        return classSym.name.toString();
    }

    /**
     * 判断类是不是有某个修饰符
     *
     * @return 如果类有某个修饰符的返回true，否则返回false
     */
    public boolean hasModifier(int modifier) {
        return classDecl.mods.flags % (modifier * 2) >= modifier;
    }

    /**
     * 添加接口
     *
     * @param interfaceClass 接口类
     * @return 返回当前类
     */
    public JcaClass addInterface(Class<?> interfaceClass) {
        // 判断类有没有实现此接口
        if (!hasInterface(interfaceClass)) {
            // 导包（会自动去重）
            importPackage(this, interfaceClass);

            java.util.List<JCTree.JCExpression> implementing = classDecl.implementing;
            ListBuffer<JCTree.JCExpression> statements = new ListBuffer<>();
            for (JCTree.JCExpression impl : implementing) {
                statements.append(impl);
            }

            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Sequence.nextLong(), names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            classDecl.implementing = statements.toList();
        }
        return this;
    }

    /**
     * 调用一个静态无参方法
     *
     * @param jcaClass   所在的类
     * @param clazz      目标类
     * @param methodName 方法名
     * @return 方法调用结果
     */
    public static JcaObject staticMethod(JcaClass jcaClass, Class<?> clazz, String methodName) {
        importPackage(jcaClass, clazz);
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(clazz.getSimpleName())), names.fromString(methodName));
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
        return new JcaObject(methodInvocation);
    }

    /**
     * 设置类的修饰符
     *
     * @param modifier 修饰符
     * @return 返回当前类
     */
    public JcaClass setModifier(int modifier) {
        classDecl.mods.flags = modifier;
        return this;
    }

    /**
     * 设置无参数私有构造器
     *
     * @return 返回当前类
     */
    public JcaClass setNoArgPrivateConstructor() {
        // 遍历类的所有字段和方法
        for (JCTree jcTree : classDecl.defs) {
            // 只处理方法
            if (jcTree instanceof JCTree.JCMethodDecl) {
                JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) jcTree;
                // 如果是构造方法 并且 没有参数
                if (JcaConstants.CONSTRUCTOR_NAME.equals(methodDecl.name.toString()) && methodDecl.params.isEmpty()) {
                    // 把修饰符改为private
                    methodDecl.mods.flags = Flags.PRIVATE;
                }
            }
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JcaClass jcaClass = (JcaClass) o;

        return classSym != null ? classSym.equals(jcaClass.classSym) : jcaClass.classSym == null;
    }

    @Override
    public int hashCode() {
        return classSym != null ? classSym.hashCode() : 0;
    }

    /**
     * 判断有没有实现指定的接口
     *
     * @param interfaceClass 接口
     * @return 如果类已经实现了指定接口则返回true，否则返回false
     */
    private boolean hasInterface(Class<?> interfaceClass) {
        for (JCTree.JCExpression impl : classDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在字段
     *
     * @param fieldName 字段名
     * @return 若存在返回true，否则返回false
     */
    private boolean existsField(String fieldName) {
        for (JCTree jcTree : classDecl.defs) {
            if (jcTree.getKind() == Tree.Kind.VARIABLE) {
                JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) jcTree;
                if (fieldName.equals(var.name.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
