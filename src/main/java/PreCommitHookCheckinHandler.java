import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.actions.VcsContextFactory;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vcs.changes.ui.VcsTreeModelData;
import com.intellij.openapi.vcs.checkin.VcsCheckinHandlerFactory;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.openapi.vcs.impl.PartialChangesUtil;
import com.intellij.openapi.vcs.vfs.VcsFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.PostponableLogRefresher;
import com.intellij.vcs.log.impl.VcsLogContentProvider;
import com.intellij.vcs.log.impl.VcsLogContentUtil;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.util.PairConsumer;
import com.intellij.vcs.log.ui.actions.VcsShowLogAction;
import com.intellij.vcs.log.util.VcsUserUtil;
import com.intellij.vcsUtil.VcsFileUtil;
import com.intellij.vcsUtil.VcsUtil;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class PreCommitHookCheckinHandler extends CheckinHandler {
    private static final String title = "Pre Commit Hook Plugin";
    private final Project project;
    private final CheckinProjectPanel checkinProjectPanel;

    PreCommitHookCheckinHandler(final CheckinProjectPanel checkinProjectPanel) {
        this.project = checkinProjectPanel.getProject();
        this.checkinProjectPanel = checkinProjectPanel;
    }

    public ReturnResult beforeCheckin(CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (DumbService.getInstance(project).isDumb()) {
            Messages.showErrorDialog(project, "Cannot commit right now because IDE updates the indices " +
                            "of the project in the background. Please try again later.",
                    title);
            return ReturnResult.CANCEL;
        }
        final List<String> changedFiles = getChanges();
        final AbstractVcs vcs = VcsUtil.getVcsFor(project, VcsUtil.getFilePath(new File(changedFiles.get(0))));
        final Collection<VirtualFile> a = checkinProjectPanel.getVirtualFiles();
        final Collection<Change> b = checkinProjectPanel.getSelectedChanges();
        final AbstractVcs c = ChangesUtil.getVcsForFile(new File(changedFiles.get(0)), project);
        VcsLogContentProvider instance = VcsLogContentProvider.getInstance(project);
        Messages.showMessageDialog(project, changedFiles.stream().collect(Collectors.joining("\n")), "Files to Commit", Messages.getInformationIcon());
        return ReturnResult.COMMIT;
    }

    private List<String> getChanges() {
        List<File> a = new ArrayList<>(checkinProjectPanel.getFiles());
        return a.stream().map(File::toString).collect(Collectors.toCollection(ArrayList::new));
    }

}

