package jus.poc.rw.v2;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Resourcev2 extends Resource {
		// Nombre de lecteurs en cours
		protected int nbR=0;
		// Nombre de redacteurs en cours
		protected int nbW=0;
		// Nombre de lectures a realiser avant une nouvelle ecriture sur la ressource
		protected int nbLect=0; 

		/**
		 * Constructor
		 * @param IDetector arg0
		 * @param IObservator obs
		 */
		public Resourcev2(IDetector arg0, IObservator obs) {
			super(arg0, obs);
		}

		/**
		 * Actor arg0 starts reading
		 */
		public synchronized void beginR(Actor arg0) throws InterruptedException, DeadLockException {
			while (nbW>0) { wait(); }
			nbR++;
			nbLect--;
			System.out.println("Le lecteur " +arg0.ident()+ " lit la ressource " +this.ident());
			super.observator.acquireResource(arg0, this);
		}

		/**
		 * Actor arg0 starts writing
		 */
		public synchronized void beginW(Actor arg0) throws InterruptedException, DeadLockException {
			while (nbR>0 || nbW>0 || nbLect>0) { wait(); }
			nbW++;
			System.out.println("Le redacteur " +arg0.ident()+ " ecrit dans la ressource " +this.ident());
			super.observator.acquireResource(arg0, this);
		}

		/**
		 * Actor arg0 stops reading
		 */
		public synchronized void endR(Actor arg0) throws InterruptedException {
			nbR--;
			System.out.println("Le lecteur " +arg0.ident()+ " arrete de lire la ressource " +this.ident());
			super.observator.releaseResource(arg0, this);
			notifyAll();
		}

		/**
		 * Actor arg0 stops writing
		 */
		public synchronized void endW(Actor arg0) throws InterruptedException {
			nbW--;
			nbLect = Simulator.NB_READERS;
			System.out.println("Le redacteur " +arg0.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
			super.observator.releaseResource(arg0, this);
			notifyAll();
		}

		public void init(Object arg0) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Methode impossible pour le moment");
		}
		
		public int getnbLect() {
			return this.nbLect;
		}

}
