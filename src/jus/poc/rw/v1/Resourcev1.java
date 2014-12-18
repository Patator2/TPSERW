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
	 * L'Actor arg0 commence a lire
	 */
	public void beginR(Actor arg0) throws InterruptedException, DeadLockException {
		rrwl.readLock().lock();
		System.out.println("Le lecteur " +arg0.ident()+ " lit la ressource " +this.ident());
		super.observator.acquireResource(arg0, this);
	}

	/**
	 * L'Actor arg0 commence a ecrire
	 */
	public void beginW(Actor arg0) throws InterruptedException, DeadLockException {
		rrwl.writeLock().lock();
		System.out.println("Le redacteur " +arg0.ident()+ " ecrit dans la ressource " +this.ident());
		super.observator.acquireResource(arg0, this);
	}

	/**
	 * L'Actor arg0 s'arrete de lire
	 */
	public void endR(Actor arg0) throws InterruptedException {
		rrwl.readLock().unlock();
		System.out.println("Le lecteur " +arg0.ident()+ " arrete de lire la ressource " +this.ident());
		super.observator.releaseResource(arg0, this);
	}

	/**
	 * L'Actor arg0 s'arrete d'ecrire
	 */
	public void endW(Actor arg0) throws InterruptedException {
		rrwl.writeLock().unlock();
		System.out.println("Le redacteur " +arg0.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
		super.observator.releaseResource(arg0, this);
	}

	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Methode impossible pour le moment");
	}

}
