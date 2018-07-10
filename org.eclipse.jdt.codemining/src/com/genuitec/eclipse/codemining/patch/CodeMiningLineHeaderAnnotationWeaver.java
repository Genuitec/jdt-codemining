/*
 * Copyright 2018, Genuitec, LLC
 * All Rights Reserved.
 */
package com.genuitec.eclipse.codemining.patch;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.genuitec.eclipse.patches.AbstractClassPatch;
import com.genuitec.eclipse.patches.PartiallyAppliedPatchException;

/**
 * Fixes two primary issues with line header code mining.
 * 
 * Issue 1 - When annotations are drawn, there is often a refresh issue resulting in some
 * screen cheese/artifacts. Adding a redraw of the StyledText after drawing the annotations
 * fixes the fact that some other text can shift when annotations appear after scroll.
 * 
 * Issue 2 - The line heading code mining extension allocates the standard editor font which
 * is way too big and becomes distracting during development. This code will drop the font
 * by 3 sizes. To minimize the number of classes changed, this caches the font on the object.
 * 
 * @author timwebb
 */
public class CodeMiningLineHeaderAnnotationWeaver extends AbstractClassPatch {

	@SuppressWarnings("nls")
	@Override
	protected boolean weave(ClassNode cw) throws PartiallyAppliedPatchException {

		// See if the class has already been woven
		if (findField(cw, "_cachedFD") != null)
			return false;
		
		/* Fields added for simple font adjustments, and redrawing:
		 * ---- code woven:
			private FontData _cachedFD;
			private Font _cachedFont;
			private int _cachedHeight;
			private static boolean _redrawScheduled= false;
		 */
		FieldVisitor fv;
		{
			fv = cw.visitField(ACC_PRIVATE, "_cachedFD", "Lorg/eclipse/swt/graphics/FontData;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "_cachedFont", "Lorg/eclipse/swt/graphics/Font;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "_cachedHeight", "I", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "_redrawScheduled", "Z", null, null);
			fv.visitEnd();
		}

		/* Replaces getHeight method:
		 * ---- code woven:
			public int getHeight() {
				initFont(getTextWidget());
				return hasAtLeastOneResolvedMiningNotEmpty() ? _cachedHeight : 0;
			}
		 */
		removeMethod(cw, "getHeight", "()I");
		{
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getHeight", "()I", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(91, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "getTextWidget", "()Lorg/eclipse/swt/custom/StyledText;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "initFont", "(Lorg/eclipse/swt/custom/StyledText;)Lorg/eclipse/swt/graphics/Font;", false);
			mv.visitInsn(POP);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(92, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "hasAtLeastOneResolvedMiningNotEmpty", "()Z", false);
			Label l2 = new Label();
			mv.visitJumpInsn(IFEQ, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedHeight", "I");
			Label l3 = new Label();
			mv.visitJumpInsn(GOTO, l3);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
			mv.visitInsn(IRETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation;", null, l0, l4, 0);
			mv.visitMaxs(2, 1);
			mv.visitEnd();
		}
		
		/* Creates the font to use for the line annotations:
		 * ---- code woven:
			protected Font initFont(StyledText textWidget) {
				// TODO should be configurable by the user, not calculated
				FontData[] fd= textWidget.getFont().getFontData();
				if (_cachedFD != null && _cachedFD.equals(fd[0])) {
					return _cachedFont;
				}
				if (_cachedFont != null)
					_cachedFont.dispose();
				_cachedFD= fd[0];
				_cachedFont= new Font(textWidget.getDisplay(), fd[0].getName(), fd[0].getHeight() - 3, SWT.NONE);
				GC gc= new GC(textWidget);
				gc.setFont(_cachedFont);
				_cachedHeight= gc.getFontMetrics().getHeight();
				gc.dispose();
				return _cachedFont;
			}
		 */
		{
			MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "initFont", "(Lorg/eclipse/swt/custom/StyledText;)Lorg/eclipse/swt/graphics/Font;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(207, l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/custom/StyledText", "getFont", "()Lorg/eclipse/swt/graphics/Font;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/Font", "getFontData", "()[Lorg/eclipse/swt/graphics/FontData;", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(208, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFD", "Lorg/eclipse/swt/graphics/FontData;");
			Label l2 = new Label();
			mv.visitJumpInsn(IFNULL, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFD", "Lorg/eclipse/swt/graphics/FontData;");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/FontData", "equals", "(Ljava/lang/Object;)Z", false);
			mv.visitJumpInsn(IFEQ, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(209, l3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			mv.visitInsn(ARETURN);
			mv.visitLabel(l2);
			mv.visitLineNumber(211, l2);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"[Lorg/eclipse/swt/graphics/FontData;"}, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			Label l4 = new Label();
			mv.visitJumpInsn(IFNULL, l4);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(212, l5);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/Font", "dispose", "()V", false);
			mv.visitLabel(l4);
			mv.visitLineNumber(213, l4);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(AALOAD);
			mv.visitFieldInsn(PUTFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFD", "Lorg/eclipse/swt/graphics/FontData;");
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLineNumber(214, l6);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "org/eclipse/swt/graphics/Font");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/custom/StyledText", "getDisplay", "()Lorg/eclipse/swt/widgets/Display;", false);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/FontData", "getName", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/FontData", "getHeight", "()I", false);
			mv.visitInsn(ICONST_3);
			mv.visitInsn(ISUB);
			mv.visitInsn(ICONST_0);
			mv.visitMethodInsn(INVOKESPECIAL, "org/eclipse/swt/graphics/Font", "<init>", "(Lorg/eclipse/swt/graphics/Device;Ljava/lang/String;II)V", false);
			mv.visitFieldInsn(PUTFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			Label l7 = new Label();
			mv.visitLabel(l7);
			mv.visitLineNumber(215, l7);
			mv.visitTypeInsn(NEW, "org/eclipse/swt/graphics/GC");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, "org/eclipse/swt/graphics/GC", "<init>", "(Lorg/eclipse/swt/graphics/Drawable;)V", false);
			mv.visitVarInsn(ASTORE, 3);
			Label l8 = new Label();
			mv.visitLabel(l8);
			mv.visitLineNumber(216, l8);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "setFont", "(Lorg/eclipse/swt/graphics/Font;)V", false);
			Label l9 = new Label();
			mv.visitLabel(l9);
			mv.visitLineNumber(217, l9);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "getFontMetrics", "()Lorg/eclipse/swt/graphics/FontMetrics;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/FontMetrics", "getHeight", "()I", false);
			mv.visitFieldInsn(PUTFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedHeight", "I");
			Label l10 = new Label();
			mv.visitLabel(l10);
			mv.visitLineNumber(218, l10);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "dispose", "()V", false);
			Label l11 = new Label();
			mv.visitLabel(l11);
			mv.visitLineNumber(219, l11);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_cachedFont", "Lorg/eclipse/swt/graphics/Font;");
			mv.visitInsn(ARETURN);
			Label l12 = new Label();
			mv.visitLabel(l12);
			mv.visitLocalVariable("this", "Lorg/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation;", null, l0, l12, 0);
			mv.visitLocalVariable("textWidget", "Lorg/eclipse/swt/custom/StyledText;", null, l0, l12, 1);
			mv.visitLocalVariable("fd", "[Lorg/eclipse/swt/graphics/FontData;", null, l1, l12, 2);
			mv.visitLocalVariable("gc", "Lorg/eclipse/swt/graphics/GC;", null, l8, l12, 3);
			mv.visitMaxs(7, 4);
			mv.visitEnd();
		}
		
		/* Rename and add redraw scheduling as part of redraw handling:
		 * ---- class dropped in:
			private static final class RedrawRunnable implements Runnable {
				private final StyledText fText;
		
				private RedrawRunnable(StyledText text) {
					fText= text;
				}
		
				@Override
				public void run() {
					if (!fText.isDisposed()) {
						_redrawScheduled= false;
						fText.redraw();
					}
				}
			}
		 * ---- code woven:
			public void redraw() {
				_redraw();
				// all minings are resolved, redraw the annotation
				StyledText text= getTextWidget();
				if (!_redrawScheduled) {
					_redrawScheduled= true;
					text.getDisplay().asyncExec(new RedrawRunnable(text));
				}
			}
		 */
		MethodNode m = findMethod(cw, "redraw", "()V");
		if (m != null) {
			m.name = "_redraw";
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "redraw", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(256, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_redraw", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(258, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "getTextWidget", "()Lorg/eclipse/swt/custom/StyledText;", false);
			mv.visitVarInsn(ASTORE, 1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(259, l2);
			mv.visitFieldInsn(GETSTATIC, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_redrawScheduled", "Z");
			Label l3 = new Label();
			mv.visitJumpInsn(IFNE, l3);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLineNumber(260, l4);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTSTATIC, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "_redrawScheduled", "Z");
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitLineNumber(261, l5);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/custom/StyledText", "getDisplay", "()Lorg/eclipse/swt/widgets/Display;", false);
			mv.visitTypeInsn(NEW, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation$RedrawRunnable");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESPECIAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation$RedrawRunnable", "<init>", "(Lorg/eclipse/swt/custom/StyledText;Lorg/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation$RedrawRunnable;)V", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/widgets/Display", "asyncExec", "(Ljava/lang/Runnable;)V", false);
			mv.visitLabel(l3);
			mv.visitLineNumber(263, l3);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"org/eclipse/swt/custom/StyledText"}, 0, null);
			mv.visitInsn(RETURN);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitLocalVariable("this", "Lorg/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation;", null, l0, l6, 0);
			mv.visitLocalVariable("text", "Lorg/eclipse/swt/custom/StyledText;", null, l2, l6, 1);
			mv.visitMaxs(5, 2);
			mv.visitEnd();
		}


		/* Change the initGC method to also initialize the font to be used:
		 * ---- code woven:
			private void initGC(StyledText textWidget, Color color, GC gc) {
				gc.setForeground(color);
				gc.setBackground(textWidget.getBackground());
				gc.setFont(initFont(textWidget));
			}
		 */
		removeMethod(cw, "initGC", "(Lorg/eclipse/swt/custom/StyledText;Lorg/eclipse/swt/graphics/Color;Lorg/eclipse/swt/graphics/GC;)V");
		{
			MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "initGC", "(Lorg/eclipse/swt/custom/StyledText;Lorg/eclipse/swt/graphics/Color;Lorg/eclipse/swt/graphics/GC;)V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(199, l0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "setForeground", "(Lorg/eclipse/swt/graphics/Color;)V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(200, l1);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/custom/StyledText", "getBackground", "()Lorg/eclipse/swt/graphics/Color;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "setBackground", "(Lorg/eclipse/swt/graphics/Color;)V", false);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(201, l2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation", "initFont", "(Lorg/eclipse/swt/custom/StyledText;)Lorg/eclipse/swt/graphics/Font;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "org/eclipse/swt/graphics/GC", "setFont", "(Lorg/eclipse/swt/graphics/Font;)V", false);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLineNumber(202, l3);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", "Lorg/eclipse/jface/internal/text/codemining/CodeMiningLineHeaderAnnotation;", null, l0, l4, 0);
			mv.visitLocalVariable("textWidget", "Lorg/eclipse/swt/custom/StyledText;", null, l0, l4, 1);
			mv.visitLocalVariable("color", "Lorg/eclipse/swt/graphics/Color;", null, l0, l4, 2);
			mv.visitLocalVariable("gc", "Lorg/eclipse/swt/graphics/GC;", null, l0, l4, 3);
			mv.visitMaxs(3, 4);
			mv.visitEnd();
		}

		return true;
	}

}
