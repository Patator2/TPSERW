package jus.poc.rw;

import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;

public class Reader extends Actor {

	/**
	 * Constructor
	 * @param useLaw the gaussian law for using delay
	 * @param vacationLaw the gaussian law for the vacation delay
	 * @param iterationLaw the gaussian law for the number of iteration do do
	 * @param selection the resources to used
	 * @param observator th observator of the comportment
	 */
	public Reader(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw, IResource[] selection, IObservator observator) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator);
	}

	/**
	 * acquisition proceeding of the reader
	 * @param resource the required resource
	 * @throws InterruptedException
	 * @throws DeadLockException
	 */
	protected void acquire(IResource resource) throws InterruptedException, DeadLockException {
		super.observator.requireResource(this, resource);
		resource.beginR(this);
	}

	/**
	 * restitution proceeding sof the reader
	 * @param resource
	 * @throws InterruptedException
	 */
	protected void release(IResource resource) throws InterruptedException {
		resource.endR(this);
		super.observator.releaseResource(this, resource);
	}

}
