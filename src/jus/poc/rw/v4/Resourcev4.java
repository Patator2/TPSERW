package jus.poc.rw.v4;

import java.util.concurrent.Semaphore;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Resourcev4 extends Resource {

	// Nombre de Readers en cours
	protected int nbR;
	// Nombre de Writers en cours
	protected int nbW;
	
	protected Semaphore mutex;
	
	protected Semaphore res;
	
	/**
	 * Constructor
	 * @param IDetector arg0
	 * @param IObservator arg1
	 */
	public Resourcev4(IDetector arg0, IObservator arg1) {
		super(arg0, arg1);
		nbR = 0;
		nbW = 0;
		mutex = new Semaphore(1);
		res = new Semaphore(1);
	}

	/**
	 * Actor arg0 starts reading
	 */
	public void beginR(Actor arg0) throws InterruptedException, DeadLockException {
		//System.out.println("Reader " +arg0.ident()+ " waits for resource " +this.ident());
		detector.waitResource(arg0, this);
		
		mutex.acquire();
		
		if (nbR==0){
			mutex.release();
			res.acquire();
			mutex.acquire();
		}

		nbR++;
		System.out.println("Reader " +arg0.ident()+ " reads resource " +this.ident());
		observator.acquireResource(arg0, this);
		detector.useResource(arg0, this);

		mutex.release();
	}

	/**
	 * Actor arg0 starts writing
	 */
	public void beginW(Actor arg0) throws InterruptedException, DeadLockException {
		//System.out.println("Writer " +arg0.ident()+ " waits for resource " +this.ident());
		detector.waitResource(arg0, this);
			
		res.acquire();

		System.out.println("Writer " +arg0.ident()+ " writes resource " +this.ident());
		observator.acquireResource(arg0, this);
		detector.useResource(arg0, this);
	}

	/**
	 * Actor arg0 stops reading
	 */
	public void endR(Actor arg0) throws InterruptedException {
		mutex.acquire();
		
		nbR--;
		System.out.println("Reader " +arg0.ident()+ " releases resource " +this.ident());
		detector.freeResource(arg0, this);

		if (nbR==0){
			res.release();
		}
		
		mutex.release();
	}

	/**
	 * Actor arg0 stops writing
	 */
	public void endW(Actor arg0) throws InterruptedException {
		System.out.println("Writer " +arg0.ident()+ " releases resource " +this.ident());
		detector.freeResource(arg0, this);

		res.release();
	}

	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}