package jus.poc.rw.v4;

import java.util.concurrent.Semaphore;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Resourcev4 extends Resource {

	// Nombre de lecteurs en cours
	protected int nbR=0;
	// Nombre de redacteurs en cours
	protected int nbW=0;
	
	protected Semaphore mutex=new Semaphore(1);
	
	protected Semaphore res=new Semaphore(1);
	
	/**
	 * Constructor
	 * @param IDetector arg0
	 * @param IObservator arg1
	 */
	public Resourcev4(IDetector arg0, IObservator arg1) {
		super(arg0, arg1);
	}

	/**
	 * Actor arg0 starts reading
	 */
	public void beginR(Actor arg0) throws InterruptedException, DeadLockException {
		//System.out.println("Le lecteur " +arg0.ident()+ " attend la ressource " +this.ident());
		detector.waitResource(arg0, this);
		
		mutex.acquire();
		
		if (nbR==0){
			res.acquire();
		}

		nbR++;
		System.out.println("Le lecteur " +arg0.ident()+ " lit la ressource " +this.ident());
		observator.acquireResource(arg0, this);
		detector.useResource(arg0, this);

		mutex.release();
	}

	/**
	 * Actor arg0 starts writing
	 */
	public void beginW(Actor arg0) throws InterruptedException, DeadLockException {
		//System.out.println("Le redacteur " +arg0.ident()+ " attend la ressource " +this.ident());
		detector.waitResource(arg0, this);
			
		res.acquire();

		System.out.println("Le redacteur " +arg0.ident()+ " ecrit dans la ressource " +this.ident());
		observator.acquireResource(arg0, this);
		detector.useResource(arg0, this);
	}

	/**
	 * Actor arg0 stops reading
	 */
	public void endR(Actor arg0) throws InterruptedException {
		mutex.acquire();
		
		nbR--;
		System.out.println("Le lecteur " +arg0.ident()+ " arrete de lire la ressource " +this.ident());
		observator.releaseResource(arg0, this);
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
		System.out.println("Le redacteur " +arg0.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
		observator.releaseResource(arg0, this);
		detector.freeResource(arg0, this);

		res.release();
	}

	public void init(Object arg0) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Methode impossible pour le moment");
	}

}