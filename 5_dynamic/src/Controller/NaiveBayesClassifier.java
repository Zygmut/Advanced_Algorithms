package Controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NaiveBayesClassifier implements Serializable {

	private static final long serialVersionUID = 1065687354L;

	private Map<String, Integer> classCounts;
	private Map<String, Integer> wordCounts;
	private Map<String, Map<String, Integer>> wordClassCounts;
	private Set<String> vocabulary;

	public NaiveBayesClassifier() {
		classCounts = new HashMap<>();
		wordCounts = new HashMap<>();
		wordClassCounts = new HashMap<>();
		vocabulary = new HashSet<>();
	}

	public void train(List<String> trainingData, List<String> labels) {
		for (int i = 0; i < trainingData.size(); i++) {
			String sentence = trainingData.get(i);
			String label = labels.get(i);

			List<String> words = Arrays.asList(sentence.split("\\s+"));

			// Preprocess the data
			words = preprocessWords(words);

			// Build the vocabulary
			vocabulary.addAll(words);

			// Update class counts
			classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);

			// Update word counts and word-class counts
			for (String word : words) {
				wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
				wordClassCounts.putIfAbsent(word, new HashMap<>());
				Map<String, Integer> classCountsForWord = wordClassCounts.get(word);
				classCountsForWord.put(label, classCountsForWord.getOrDefault(label, 0) + 1);
			}
		}
	}

	public Object[] classify(String sentence) {
		List<String> words = Arrays.asList(sentence.split("\\s+"));

		// Preprocess the data
		words = preprocessWords(words);

		List<String> labels = new ArrayList<>();
		List<Double> scores = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : classCounts.entrySet()) {
			String label = entry.getKey();
			double score = Math.log(classCounts.get(label) / (double) classCounts.size());

			for (String word : words) {
				int wordCount = wordCounts.getOrDefault(word, 0);
				int wordClassCount = wordClassCounts.getOrDefault(word, new HashMap<>()).getOrDefault(label, 0);
				double wordProb = (wordClassCount + 1) / (double) (wordCount + vocabulary.size());

				score += Math.log(wordProb);
			}

			labels.add(label);
			scores.add(score);
		}

		// Convert scores to probabilities using softmax
		double[] probabilities = softmax(scores.toArray(Double[]::new));

		return new Object[] { labels, probabilities };
	}

	private double[] softmax(Double[] scores) {
		int n = scores.length;
		double[] softmaxProbabilities = new double[n];

		// Find the maximum score value
		double maxScore = Double.NEGATIVE_INFINITY;
		for (double score : scores) {
			if (score > maxScore) {
				maxScore = score;
			}
		}

		// Compute the sum of exponentiated scores (in log domain)
		double sumExpScores = 0.0;
		for (double score : scores) {
			double shiftedScore = score - maxScore;
			sumExpScores += Math.exp(shiftedScore);
		}

		// Compute softmax probabilities (in log domain)
		assert sumExpScores > 0.0;
		for (int i = 0; i < n; i++) {
			double shiftedScore = scores[i] - maxScore;
			softmaxProbabilities[i] = Math.exp(shiftedScore) / sumExpScores;
		}

		return softmaxProbabilities;
	}

	public void saveModel(String filePath) {
		try (FileOutputStream fileOut = new FileOutputStream(filePath);
				BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
				ObjectOutputStream objectOut = new ObjectOutputStream(bufferedOut);) {
			objectOut.writeObject(this);
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Model saved to: {0}", filePath);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error saving the model: {0}",
					e.getMessage());
		}
	}

	public static NaiveBayesClassifier loadModel(String filePath) {
		try (FileInputStream fileIn = new FileInputStream(filePath);
				BufferedInputStream bufferedIn = new BufferedInputStream(fileIn);
				ObjectInputStream objectIn = new ObjectInputStream(bufferedIn);) {
			Object obj = objectIn.readObject();
			if (obj instanceof NaiveBayesClassifier) {
				return (NaiveBayesClassifier) obj;
			}
		} catch (Exception e) {
			Logger.getLogger(NaiveBayesClassifier.class.getName()).log(Level.SEVERE, "Error loading the model: {0}",
					e.getMessage());
		}
		return null;
	}

	public byte[] saveModelToByte() {
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			BufferedOutputStream bufferedOut = new BufferedOutputStream(byteArrayOut);
			ObjectOutputStream objectOut = new ObjectOutputStream(bufferedOut);
			objectOut.writeObject(this);
			objectOut.close();
			byteArrayOut.close();
			return byteArrayOut.toByteArray();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error saving the model: {0}",
					e.getMessage());
		}
		return new byte[0];
	}

	public static NaiveBayesClassifier loadModelFromByte(byte[] data) {
		try {
			ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(data);
			BufferedInputStream bufferedIn = new BufferedInputStream(byteArrayIn);
			ObjectInputStream objectIn = new ObjectInputStream(bufferedIn);
			NaiveBayesClassifier classifier = (NaiveBayesClassifier) objectIn.readObject();
			objectIn.close();
			byteArrayIn.close();
			return classifier;
		} catch (IOException | ClassNotFoundException e) {
			Logger.getLogger(NaiveBayesClassifier.class.getName()).log(Level.SEVERE, "Error loading the model: {0}",
					e.getMessage());
		}
		return null;
	}

	private List<String> preprocessWords(List<String> words) {
		List<String> processedWords = new ArrayList<>();

		for (String word : words) {
			// The word should be in lowercase
			word = word.toLowerCase();
			// The word should not contain any punctuation
			word = word.replaceAll("[^a-zA-Z0-9]", "");
			// The word should not contain any numbers
			word = word.replaceAll("[\\d]", "");
			// The word should not contain any special characters
			word = word.replaceAll("[^\\p{ASCII}]", "");
			// The word should not contain any non-english characters
			word = word.replaceAll("[^\\p{IsLatin}]", "");
			// The word should not contain any exclamations or question marks
			word = word.replaceAll("[!|?]", "");

			// It should not be empty
			if (word.isEmpty() || word.isBlank() || word.length() == 0) {
				continue;
			}
			processedWords.add(word);
		}

		return processedWords;
	}

}
