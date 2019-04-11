package examples;

import java.util.Collection;
import java.util.Random;

import javax.security.auth.Subject;

import tests.versioning.VersionedDocument;
import tests.versioning.VersionedDocumentAndVersioningRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.versions.VersionInfo;

import static java.lang.String.format;

public class ThreadTest implements Runnable {

    private final int index;
	private final VersionedDocumentAndVersioningRepository repo;
	private final boolean createPrivateWorkingCopy;

    public ThreadTest(int i, VersionedDocumentAndVersioningRepository repo, boolean createPrivateWorkingCopy) {
        this.index = i;
        this.repo = repo;
        this.createPrivateWorkingCopy = createPrivateWorkingCopy;
    }

    @Override
    public void run() {
        int max = 100;
        int min = 1;
        Random randomNum = new Random();
        int delay = min + randomNum.nextInt(max);

        securityContext("user-" + index);
        System.out.println(format("User %s delay is %s", "user-" + index, delay));

        for (int i=0; i < 10; i++) {
            VersionedDocument doc = new VersionedDocument();
            doc = repo.save(doc);

            try {
//                Thread.sleep(delay);
                System.out.println(format("User %s locking %s", "user-" + index, doc.getId()));
                System.out.flush();
                doc = repo.lock(doc);

                if (createPrivateWorkingCopy) {
//                Thread.sleep(delay);
					System.out.println(format("User %s creating private working copy %s", "user-" + index, doc.getId()));
					System.out.flush();
                	doc = repo.workingCopy(doc);
				}

//                Thread.sleep(delay);
                System.out.println(format("User %s saving %s", "user-" + index, doc.getId()));
                System.out.flush();
                doc.setData("foobar");
                doc = repo.save(doc);

//                Thread.sleep(delay);
                System.out.println(format("User %s versioning %s", "user-" + index, doc.getId()));
                System.out.flush();
                doc = repo.version(doc, new VersionInfo("1.1", "minor changes"));
                if (doc.getLockOwner() == null) {
                    throw new IllegalStateException(format("versioning %s failed", doc.getId()));
                }

//                Thread.sleep(delay);
                System.out.println(format("User %s unlocking %s", "user-" + index, doc.getId()));
                System.out.flush();
                doc = repo.unlock(doc);

//                Thread.sleep(delay);
				System.out.println(format("User %s deleting %s", "user-" + index, doc.getId()));
				System.out.flush();
				repo.delete(doc);
            } catch (Exception e) {
                System.out.println(format("User %s id %s", "user-" + index, doc.getId()));
                e.printStackTrace();
            }
        }
    }

    private static void securityContext(String user) {
        SecurityContextHolder.setContext(new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return new Authentication() {
                    @Override
                    public String getName() {
                        return user;
                    }

                    @Override
                    public boolean implies(Subject subject) {
                        return false;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public Object getPrincipal() {
                        return user;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return true;
                    }

                    @Override
                    public void setAuthenticated(boolean b) throws IllegalArgumentException {

                    }
                };
            }

            @Override
            public void setAuthentication(Authentication authentication) {

            }
        });
    }
}
