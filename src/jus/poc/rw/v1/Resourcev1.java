package jus.poc.rw.v1;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Resourcev1 extends Resource {
	
	protected ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();

	/**
	 * Constructor
	 * @param IDetector det
	 * @param IObservator obs
	 */
	public Resourcev1(IDetector det, IObservator obs) {
		super(det, obs);
	}

	/**
	 * L'Actor act commence a lire
	 */
	public void beginR(Actor act) throws InterruptedException, DeadLockException {
		rrwl.readLock().lock();
		System.out.println("Le lecteur " +act.ident()+ " lit la ressource " +this.ident());
		super.observator.acquireResource(act, this);
	}

	/**
	 * L'Actor act commence a ecrire
	 */
	public void beginW(Actor act) throws InterruptedException, DeadLockException {
		rrwl.writeLock().lock();
		System.out.println("Le redacteur " +act.ident()+ " ecrit dans la ressource " +this.ident());
		super.observator.acquireResource(act, this);
	}

	/**
	 * L'Actor act s'arrete de lire
	 */
	public void endR(Actor act) throws InterruptedException {
		rrwl.readLock().unlock();
		System.out.println("Le lecteur " +act.ident()+ " arrete de lire la ressource " +this.ident());
	}

	/**
	 * L'Actor act s'arrete d'ecrire
	 */
	public void endW(Actor act) throws InterruptedException {
		rrwl.writeLock().unlock();
		System.out.println("Le redacteur " +act.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
	}

	public void init(Object act) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Methode impossible pour le moment");
	}

}
