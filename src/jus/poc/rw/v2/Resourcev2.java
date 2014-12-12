package jus.poc.rw.v2;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Resourcev2 extends Resource {
		// Nombre de lecteurs en cours
		protected int nbR;
		// Nombre de redacteurs en cours
		protected int nbW;
		// Nombre de lectures a realiser avant une nouvelle ecriture sur la ressource
		protected int nbLect; 

		/**
		 * Constructor
		 * @param IDetector act
		 * @param IObservator obs
		 */
		public Resourcev2(IDetector act, IObservator obs) {
			super(act, obs);
			nbR = 0;
			nbW = 0;
			nbLect = 0;
		}

		/**
		 * Actor act starts reading
		 */
		public synchronized void beginR(Actor act) throws InterruptedException, DeadLockException {
			while (nbW>0) { wait(); }
			nbR++;
			nbLect--;
			System.out.println("Le lecteur " +act.ident()+ " lit la ressource " +this.ident());
			super.observator.acquireResource(act, this);
		}

		/**
		 * Actor act starts writing
		 */
		public synchronized void beginW(Actor act) throws InterruptedException, DeadLockException {
			while (nbR>0 || nbW>0 || nbLect>0) { wait(); }
			nbW++;
			System.out.println("Le redacteur " +act.ident()+ " ecrit dans la ressource " +this.ident());
			super.observator.acquireResource(act, this);
		}

		/**
		 * Actor act stops reading
		 */
		public synchronized void endR(Actor act) throws InterruptedException {
			nbR--;
			System.out.println("Le lecteur " +act.ident()+ " arrete de lire la ressource " +this.ident());
			notifyAll();
		}

		/**
		 * Actor act stops writing
		 */
		public synchronized void endW(Actor act) throws InterruptedException {
			nbW--;
			nbLect = Simulator.NB_READERS;
			System.out.println("Le redacteur " +act.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
			notifyAll();
		}

		public void init(Object act) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Methode impossible pour le moment");
		}
		
		public int getnbLect() {
			return this.nbLect;
		}

}
