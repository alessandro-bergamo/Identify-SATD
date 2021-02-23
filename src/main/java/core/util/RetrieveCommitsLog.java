package core.util;

import core.entities.Commit;
import core.entities.detector.ImpossibleDetection;
import javafx.util.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;


import java.io.File;
import java.util.*;

public class RetrieveCommitsLog
{

    public RetrieveCommitsLog()
    {
        repository = null;
        logCommits = new ArrayList<>();
    }


    public Pair<String, List<Commit>> retrieveCommitsLogs(String repository_path)
    {
        try {
            repository = Git.open(new File(repository_path)).getRepository();
        } catch (Exception e) {
            throw new ImpossibleDetection();
        }

        Git git = new Git(repository);
        Iterable<RevCommit> log;

        String url = repository.getConfig().getString("remote", "origin", "url");

        try {
            log = git.log().call();
        } catch (Exception e) {
            throw new ImpossibleDetection();
        }

        for(Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();)
        {
            RevCommit rev = iterator.next();

            Commit commit = new Commit (
                    rev.getId().getName(),
                    rev.getCommitterIdent().getName(),
                    rev.getFullMessage(),
                    rev.getAuthorIdent().getWhen()
            );

            /* CODICE DI STAMPA DEI FILE MODIFICATI NEL COMMIT
            System.out.println("\nCOMMIT START ----------------------------"+commit.getCommitID()+" MESSAGE: "+commit.getCommitMessage());

            List<DiffEntry> diffs;
            try (RevWalk rw = new RevWalk(repository))
            {
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);

                if (iterator.hasNext()) {
                    RevCommit parent = rw.parseCommit(rev.getParent(0).getId());

                    diffs = df.scan(parent.getTree(), rev.getTree());
                } else {
                    diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), rev.getTree()));
                }
            } catch(Exception e) {
                throw new ImpossibleIdentification();
            }

            for (DiffEntry diff : diffs)
            {
                if(!diff.getChangeType().name().equals("DELETE"))
                    System.out.println("Change Type: " + diff.getChangeType().name() + " -/- File Path: " + diff.getNewPath());
            }

            System.out.println("COMMIT END ----------------------------");
            */

            logCommits.add(commit);
        }

        Collections.reverse(logCommits);

        repositoryUrl_commits = new Pair<>(url, logCommits);

        return repositoryUrl_commits;
    }



    private Repository repository;
    private ArrayList<Commit> logCommits;
    private Pair<String, List<Commit>> repositoryUrl_commits;

}
