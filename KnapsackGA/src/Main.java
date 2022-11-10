import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    static final double pc = 0.5;
    static final double pm = 0.01;
    static final int generationNum = 3;

    static int bestValue = 0;
    static int bestValueInd = 0;

    public static ArrayList<Integer> calcCumulativeFitness(ArrayList<Integer> fitness) {
        ArrayList<Integer> cumFitness = new ArrayList<>();
        cumFitness.add(fitness.get(0));
        for (int i = 1; i < fitness.size(); i++) {
            cumFitness.add(cumFitness.get(i - 1) + fitness.get(i));
        }
        return cumFitness;
    }

    public static ArrayList<Integer> rouletteWheel(ArrayList<Integer> fitness, int chromosomeSize) {
        Random random = new Random();

        ArrayList<Integer> cumFitness = calcCumulativeFitness(fitness);
        int lastInd = cumFitness.size() - 1;
        int totalSum = cumFitness.get(lastInd) + 1;
        ArrayList<Integer> parentsInd = new ArrayList<>();

        int selectionNum;

        for (int i = 0; i < chromosomeSize; i++) {
            selectionNum = random.nextInt(totalSum);
            for (int j = 0; j < cumFitness.size(); j++) {
                if (cumFitness.get(j) >= selectionNum) {
                    parentsInd.add(j);
                    break;
                }
            }
        }
        return parentsInd;
    }

    public static ArrayList<Integer> fitnessCalc(
            int[] weights, int[] values, ArrayList<ArrayList<Integer>> chromosomes, int itemsNum, int knapsackSize) {
        ArrayList<Integer> fitness = new ArrayList<>();

        for (ArrayList<Integer> chromosome : chromosomes) {
            int sumOfValues = 0;
            int sumOfWeights = 0;
            for (int j = 0; j < itemsNum; j++) {
                if (chromosome.get(j) == 1) {
                    sumOfValues += values[j];
                    sumOfWeights += weights[j];
                }
            }
            if (sumOfWeights > knapsackSize) {
                fitness.add(0);
            } else {
                fitness.add(sumOfValues);
            }

            bestValue = Collections.max(fitness);
            for (int i = 0; i < fitness.size(); i++) {
                if (fitness.get(i) == bestValue)
                    bestValueInd = i;
            }

        }
        return fitness;
    }

    public static ArrayList<ArrayList<Integer>> populationInit(int itemsNum) {
        Random random = new Random();
        int populationSize = random.nextInt(20 - 5) + 5;
        ArrayList<ArrayList<Integer>> chromosomes = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            chromosomes.add(new ArrayList<>());
            for (int j = 0; j < itemsNum; j++) {
                chromosomes.get(i).add(random.nextInt(2));
            }

        }
        return chromosomes;

    }

    public static ArrayList<ArrayList<Integer>> crossover(ArrayList<Integer> parentsInd, ArrayList<ArrayList<Integer>> chromosomes) {
        Random random = new Random();
        ArrayList<ArrayList<Integer>> offSprings = new ArrayList<>();
        int size = chromosomes.size();

        for (int i = 0; i < chromosomes.size(); i++) {
            offSprings.add(new ArrayList<>());
        }

        if (chromosomes.size() % 2 != 0) {

            for (int j = 0; j < chromosomes.get(1).size(); j++) {
                offSprings.get(chromosomes.size() - 1).add(chromosomes.get(parentsInd.get(chromosomes.size() - 1)).get(j));
            }

            size = chromosomes.size() - 1;
        }

        for (int i = 0; i < size; i += 2) {
            double randomProbability = random.nextDouble(1);
            int singlePointCrossover = random.nextInt((chromosomes.get(1).size() - 1) - 1) + 1;
            //System.out.println("RandomProb " + randomProbability + " SPC " + singlePointCrossover);

            if (randomProbability <= pc) {
                for (int j = 0; j < singlePointCrossover; j++) {
                    offSprings.get(i).add(chromosomes.get(parentsInd.get(i)).get(j));
                    offSprings.get(i + 1).add(chromosomes.get(parentsInd.get(i + 1)).get(j));
                }

                for (int k = singlePointCrossover; k < chromosomes.get(1).size(); k++) {
                    offSprings.get(i).add(chromosomes.get(parentsInd.get(i + 1)).get(k));
                    offSprings.get(i + 1).add(chromosomes.get(parentsInd.get(i)).get(k));

                }
            } else {
                for (int j = 0; j < chromosomes.get(1).size(); j++) {
                    offSprings.get(i).add(chromosomes.get(parentsInd.get(i)).get(j));
                    offSprings.get(i + 1).add(chromosomes.get(parentsInd.get(i + 1)).get(j));
                }
            }
        }


        return offSprings;

    }

    public static ArrayList<ArrayList<Integer>> mutation(ArrayList<ArrayList<Integer>> offSpring) {
        Random random = new Random();

        for (ArrayList<Integer> integers : offSpring) {
            for (int j = 0; j < offSpring.get(1).size(); j++) {
                double probabilityOfMutation = random.nextDouble(1);

                if (probabilityOfMutation <= pm) {
                    if (integers.get(j) == 1) {
                        integers.set(j, 0);

                    } else {
                        integers.set(j, 1);
                    }
                }
            }
        }
        return offSpring;
    }

    public static void replacement(ArrayList<ArrayList<Integer>> oldGeneration, ArrayList<ArrayList<Integer>> newGeneration) {
        for (int i = 0; i < oldGeneration.size(); i++) {
            for (int j = 0; j < oldGeneration.get(1).size(); j++) {
                oldGeneration.get(i).set(j, newGeneration.get(i).get(j));
            }
        }
    }



    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("input.txt");
        Scanner sc = new Scanner(file);
        int testCaseNum = sc.nextInt();
        int knapsackSize;
        int itemsNum;
        int[] weights;
        int[] values;
        ArrayList<ArrayList<Integer>> chromosomes;
        int chromosomesSize;
        ArrayList<Integer> fitness;
        ArrayList<Integer> parentsInd;
        ArrayList<ArrayList<Integer>> offSpring;
        ArrayList<ArrayList<Integer>> bestSolution;

        for (int i = 0; i < testCaseNum; i++) {
            knapsackSize = sc.nextInt();
            itemsNum = sc.nextInt();
            weights = new int[itemsNum];
            values = new int[itemsNum];

            for (int j = 0; j < itemsNum; j++) {
                weights[j] = sc.nextInt();
                values[j] = sc.nextInt();

            }
            System.out.println("Chromosomes ");
            System.out.println(chromosomes = populationInit(itemsNum));
            for (int k = 0; k < generationNum; k++) {
                fitness = fitnessCalc(weights, values, chromosomes, itemsNum, knapsackSize);
                chromosomesSize = chromosomes.size();
                System.out.println(parentsInd = rouletteWheel(fitness, chromosomesSize));
                System.out.println("offSprings ");
                System.out.println(offSpring = crossover(parentsInd, chromosomes));
                System.out.println("Mutation");
                System.out.println(mutation(offSpring));
                replacement(chromosomes, offSpring);
                System.out.println("NewGene");
                System.out.println(chromosomes);

            }

        }


    }
}