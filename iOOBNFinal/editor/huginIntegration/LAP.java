package huginIntegration;

import java.util.ArrayList;
import java.util.Scanner;

import COM.hugin.HAPI.*;

class LoadAndPropagate {
	/**
	 * This function parses the given NET file, compiles the network, and prints
	 * the prior beliefs and expected utilities of all nodes. If a case file is
	 * given, the function loads the file, propagates the evidence, and prints
	 * the updated results.
	 *
	 * If the network is a LIMID, we assume that we should compute policies for
	 * all decisions (rather than use the ones specified in the NET file).
	 * Likewise, we update the policies when new evidence arrives.
	 * 
	 * @throws ExceptionHugin
	 */
	static int cliqueCount = 0;

	public static void distributeEvidence(Clique self, Clique parent) throws ExceptionHugin {
		CliqueList neighbor = self.getNeighbors();
		System.out.println("Clique " + cliqueCount++ + " :");
		displayClique(self);
		if (parent != null) {
			// absorb from parent
		}
		for (Clique cl : neighbor) {
			if (cl != parent)
				distributeEvidence(cl, self);
		}
	}

	public static void displayClique(Clique cl) throws ExceptionHugin {
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes = cl.getMembers();
		for (Node node : nodes) {
			System.out.println(node.getName() + "  [" + node.getTable().getDataItem(0) + " "
					+ node.getTable().getDataItem(1) + "]"); // this returns the
																// data values
																// of a cpt in
																// single row
																// manner
			DiscreteNode dNode = (DiscreteNode) node;
			// System.out.println (node.getName() + " [ " + dNode.getBelief(0) +
			// " ]");
		}
	}

	public static void LAP(String netName, String caseName) {
		try {
			ParseListener parseListener = new DefaultClassParseListener();
			Domain domain = new Domain(netName + ".net", parseListener);

			if (domain.isCompiled()) {
				System.out.println("Domain is already compiled. We are uncompiling it!!!");
				domain.uncompile();
			}
			// else System.out.println("Domain is not compiled. We are compiling
			// it!!!");
			domain.openLogFile(netName + ".log");
			domain.triangulate(Domain.H_TM_BEST_GREEDY);

			ArrayList<JunctionTree> jTs = new ArrayList<JunctionTree>();

			jTs = domain.getJunctionTrees();
			for (JunctionTree key : jTs) {
				Clique root = key.getRoot();
				distributeEvidence(root, null);
			}

			domain.compile();
			domain.closeLogFile();

			boolean hasUtilities = containsUtilities(domain.getNodes());

			if (!hasUtilities)
				System.out.println("Prior beliefs:");
			else {
				domain.updatePolicies();
				System.out.println("Overall expected utility: " + domain.getExpectedUtility());
				System.out.println();
				System.out.println("Prior beliefs (and expected utilities):");
			}
			printBeliefsAndUtilities(domain);

			if (caseName != null) {
				domain.parseCase(caseName, parseListener);

				System.out.println();
				System.out.println();
				System.out.println("Propagating the evidence specified in \"" + caseName + "\"");

				domain.propagate(Domain.H_EQUILIBRIUM_SUM, Domain.H_EVIDENCE_MODE_NORMAL);

				System.out.println();
				System.out.println("P(evidence) = " + domain.getNormalizationConstant());
				System.out.println();

				if (!hasUtilities)
					System.out.println("Updated beliefs:");
				else {
					domain.updatePolicies();
					System.out.println("Overall expected utility: " + domain.getExpectedUtility());
					System.out.println();
					System.out.println("Updated beliefs (and expected utilities):");
				}
				printBeliefsAndUtilities(domain);
			}

			domain.delete();
		} catch (ExceptionHugin e) {
			System.out.println("Exception caught: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("General exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Print the beliefs and expected utilities of all nodes in the domain.
	 */
	public static void printBeliefsAndUtilities(Domain domain) throws ExceptionHugin {
		NodeList nodes = domain.getNodes();
		boolean hasUtilities = containsUtilities(nodes);
		java.util.ListIterator it = nodes.listIterator();

		while (it.hasNext()) {
			Node node = (Node) it.next();

			System.out.println();
			System.out.println(node.getLabel() + " (" + node.getName() + ")");

			if (node instanceof DiscreteNode) {
				DiscreteNode dNode = (DiscreteNode) node;

				for (int i = 0, n = (int) dNode.getNumberOfStates(); i < n; i++) {
					System.out.print("  - " + dNode.getStateLabel(i) + " " + dNode.getBelief(i));
					if (hasUtilities)
						System.out.println(" (" + dNode.getExpectedUtility(i) + ")");
					else
						System.out.println();
				}
			} else if (node instanceof ContinuousChanceNode) {
				ContinuousChanceNode ccNode = (ContinuousChanceNode) node;

				System.out.println("  - Mean : " + ccNode.getMean());
				System.out.println("  - SD   : " + Math.sqrt(ccNode.getVariance()));
			} else if (node instanceof UtilityNode)
				System.out.println("  - Expected utility: " + ((UtilityNode) node).getExpectedUtility());
			else /* "node" is a (real-valued) function node */
				try {
					System.out.println("  - Value: " + ((FunctionNode) node).getValue());
				} catch (ExceptionHugin e) {
					System.out.println("  - Value: N/A");
				}
		}
	}

	/**
	 * Are there utility nodes in the list?
	 */
	public static boolean containsUtilities(NodeList list) {
		java.util.ListIterator it = list.listIterator();

		while (it.hasNext())
			if (it.next() instanceof UtilityNode)
				return true;

		return false;
	}

	/**
	 * Load a Hugin NET file, compile the network, and print the results. If a
	 * case file is specified, load it, propagate the evidence, and print the
	 * results.
	 */
	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);
		if (args.length == 1)
			LAP(args[0], null);
		else if (args.length == 2)
			LAP(args[0], args[1]);
		else {
			// System.err.println ("Usage: <netName> [<caseName>]");
			System.out.println("Enter net file name:");
			String netFileName = "C:\\Users\\msamiull\\workspaceICOOBN\\NETPlusPlus\\" + "ChestClinic";
			System.out.println(netFileName);
			LAP(netFileName, null);
			// LAP (netFileName, null);
		}
	}
}