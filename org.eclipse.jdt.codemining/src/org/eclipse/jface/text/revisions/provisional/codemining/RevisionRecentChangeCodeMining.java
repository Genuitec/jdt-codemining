package org.eclipse.jface.text.revisions.provisional.codemining;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.revisions.Revision;
import org.eclipse.jface.text.revisions.RevisionRange;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeExtension;
import org.eclipse.jface.text.revisions.provisional.IRevisionRangeProvider;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class RevisionRecentChangeCodeMining extends LineHeaderCodeMining {

	private final ILineRange lineRange;
	private final IRevisionRangeProvider rangeProvider;
	private final boolean showDate;

	public RevisionRecentChangeCodeMining(int beforeLineNumber, ILineRange lineRange, IDocument document,
			boolean showAvatar, boolean showDate, ICodeMiningProvider provider, IRevisionRangeProvider rangeProvider)
			throws JavaModelException, BadLocationException {
		super(beforeLineNumber, document, provider);
		this.rangeProvider = rangeProvider;
		this.lineRange = lineRange;
		this.showDate = showDate;
		if (rangeProvider.isInitialized()) {
			updateLabel();
		}
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		if (getLabel() != null) {
			return super.doResolve(viewer, monitor);
		}
		return CompletableFuture.runAsync(() -> {
			updateLabel();
		});
	}

	private void updateLabel() {
		try {
			List<RevisionRange> ranges = rangeProvider.getRanges(lineRange);
			if (ranges != null && ranges.size() > 0) {
				Revision revision = ranges.stream().map(r -> r.getRevision())
						.max(Comparator.comparing(Revision::getDate)).get();
				if (showDate && (revision instanceof IRevisionRangeExtension)) {
					super.setLabel(
							revision.getAuthor() + ", " + ((IRevisionRangeExtension) revision).getFormattedTime());
				} else {
					super.setLabel(revision.getAuthor());
				}
			}
		} catch (Exception e) {
			super.setLabel(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		return super.draw(gc, textWidget, color, x, y);
	}

}
