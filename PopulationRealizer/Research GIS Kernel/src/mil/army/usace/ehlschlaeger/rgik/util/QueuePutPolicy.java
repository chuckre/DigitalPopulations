package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



/**
 * Implement the one policy ThreadPoolExecutor is missing: If job queue is full,
 * BLOCK! I mean, if I didn't want the executor to block, then I wouldn't be
 * giving it a BlockingQueue to begin with, would I?
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class QueuePutPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException ie) {
            throw new RejectedExecutionException(ie);
        }
    }

    /**
     * Create an executor with no job queue -- submit() will hang until a thread
     * accepts the job. Given number of threads will be spawned immediately and
     * maintained until shutdown() is called.
     * 
     * @param nThreads
     *            the number of threads in the pool
     * @return the newly created executor
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>(),
            new QueuePutPolicy());
    }

    /**
     * Create an executor with a bounded job queue -- submit() will hang when
     * the queue is full until a thread removes a job. Allow enough memory for
     * (nThreads+nJobs) task objects -- the first nThreads jobs will be run,
     * while the next nJobs jobs will be queued.
     * 
     * @param nThreads
     *            the number of threads in the pool
     * @param nJobs
     *            number of extra jobs to allow in the queue
     * @return the newly created executor
     */
    public static ExecutorService newFixedThreadPool(int nThreads, int nJobs) {
        return new ThreadPoolExecutor(nThreads, nThreads,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nJobs),
            new QueuePutPolicy());
    }
}
