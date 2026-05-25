import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SerializationHelper;

import weka.classifiers.trees.RandomForest;
import weka.classifiers.Evaluation;

import java.util.Random;
import java.util.concurrent.*;


public class iris_ml {
    public static void main(String[] args) throws Exception {
        // data

        DataSource source = new DataSource("iris.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        System.out.println("Iris Dataset:");
        System.out.println("Num of instances: " + data.numInstances());
        System.out.println("Num of features: " + (data.numAttributes() - 1));
        System.out.println("Classes: " + data.classAttribute().numValues());

        for (int i = 0; i < data.classAttribute().numValues(); i++)
        {
            System.out.println("  - " + data.classAttribute().value(i));
        }

        data.randomize(new Random(42));

        int trainSize = (int)(data.numInstances() * 0.8);
        int testSize = data.numInstances() - trainSize;

        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);

        System.out.printf("Train set: %d (%.0f%%)\n", trainSize, 80.0);
        System.out.printf("Test set: %d (%.0f%%)\n", testSize, 20.0);


        // multithreading
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<ModelResult> task1 = () -> trainAndEvaluate(trainData, testData, 2);
        Callable<ModelResult> task2 = () -> trainAndEvaluate(trainData, testData, 5);
        Callable<ModelResult> task3 = () -> trainAndEvaluate(trainData, testData, 10);

        Future<ModelResult> f1 = executor.submit(task1);
        Future<ModelResult> f2 = executor.submit(task2);
        Future<ModelResult> f3 = executor.submit(task3);

        ModelResult r1 = f1.get();
        ModelResult r2 = f2.get();
        ModelResult r3 = f3.get();
        executor.shutdown();

        printModelResults(r1);
        printModelResults(r2);
        printModelResults(r3);

        ModelResult best = r1;
        if (r2.testAccuracy > best.testAccuracy) best = r2;
        if (r3.testAccuracy > best.testAccuracy) best = r3;

        System.out.println("\nBest model: " + best.trees + " trees");
        System.out.println("Accuracy: " + best.testAccuracy + "%\n");

        SerializationHelper.write("best_model.model", best.model);


        // prediction
        Instance newFlower = new DenseInstance(data.numAttributes());
        newFlower.setDataset(data);

        newFlower.setValue(0, 5.1);
        newFlower.setValue(1, 3.5);
        newFlower.setValue(2, 1.4);
        newFlower.setValue(3, 0.2);

        double prediction = best.model.classifyInstance(newFlower);
        String predictedClass = data.classAttribute().value((int) prediction);

        System.out.println("Predicted class: " + predictedClass);
    }


    public static void printModelResults(ModelResult r) {
        System.out.printf("\n--- Model with %d trees ---\n", r.trees);
        System.out.printf("Train accuracy: %.2f%%\n", r.trainAccuracy);
        System.out.printf("Test accuracy: %.2f%%\n", r.testAccuracy);
        double gap = r.trainAccuracy - r.testAccuracy;
        System.out.printf("Overfitting: %.2f%%\n", Math.abs(gap));
    }


    public static ModelResult trainAndEvaluate(Instances trainData, Instances testData, int numTrees) throws Exception {
        RandomForest model = new RandomForest();
        model.setNumIterations(numTrees);
        model.setMaxDepth(2);

        model.buildClassifier(trainData);

        Evaluation trainEval = new Evaluation(trainData);
        trainEval.evaluateModel(model, trainData);

        Evaluation testEval = new Evaluation(trainData);
        testEval.evaluateModel(model, testData);

        return new ModelResult(model, testEval.pctCorrect(), trainEval.pctCorrect(), numTrees, trainEval, testEval);
    }


    static class ModelResult {
        RandomForest model;
        double testAccuracy;
        double trainAccuracy;
        int trees;
        Evaluation trainEval;
        Evaluation testEval;

        ModelResult(RandomForest model, double testAccuracy, double trainAccuracy, int trees,
            Evaluation trainEval, Evaluation testEval
        )
        {
            this.model = model;
            this.testAccuracy = testAccuracy;
            this.trainAccuracy = trainAccuracy;
            this.trees = trees;
            this.trainEval = trainEval;
            this.testEval = testEval;
        }
    }
}