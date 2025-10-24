package com.example.demo.service;

//
// Name: Gamboa, Leonardo
// Project #3
// Due: 03/28/2025
// Course: CS-2400-03-SP25
//
// Description:
// This class implements a Hashed Dictionary ADT using open addressing and linear probing 
// to efficiently handle collisions. It stores key-value pairs where each key is a unique 
// entry and the value represents associated data. The class includes methods for adding 
// entries, retrieving values, and checking for existing keys as well as otheer methods. 
// An iterator is provided for traversing the keys in the dictionary.
//

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Implements a dictionary using hashing. */
public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
	private static final int DEFAULT_CAPACITY = 5; // Default initial size of hash table
	private static final int MAX_CAPACITY = 10000; // Maximum allowable size
	
	private Entry<K, V>[] hashTable; // Array-based hash table
	private int tableSize; // Current size of hash table
	private int numberOfEntries; // Number of stored key-value pairs
	private boolean integrityOK = false; // Ensures integrity of hash table
	private int numberOfCollisions; // Tracks collision count
	private static final double loadFactor = 0.5; // Maximum load factor before resizing

	protected final Entry<K, V> AVAILABLE = new Entry<>(null, null); // Marker for removed entries

	/** Default constructor. */
	public HashedDictionary() 
	{
		this(DEFAULT_CAPACITY);
	}

	/** Constructor with initial capacity. */
	public HashedDictionary(int initialCapacity) 
	{
		initialCapacity = checkCapacity(initialCapacity);
		numberOfEntries = 0;
		numberOfCollisions = 0;
		tableSize = getNextPrime(initialCapacity);
		checkSize(tableSize);
		
		@SuppressWarnings("unchecked")
		Entry<K, V>[] temp = (Entry<K, V>[]) new Entry[tableSize];
		hashTable = temp;
		integrityOK = true;
	}

    /**
     * Adds a key-value pair to the dictionary.
     * If the key already exists, updates its value.
     * @param key The key to be added.
     * @param value The associated value.
     * @return The previous value associated with the key, or null if new.
     */
    	@Override
	public V add(K key, V value) 
	{
		V result = null;
		if (key == null || value == null) throw new IllegalArgumentException("Key or value is null.");
		boolean collisionIncrement = false;
		int index = getHashIndex(key);
		
		while (hashTable[index] != null && hashTable[index] != AVAILABLE) 
		{
			if (hashTable[index].key.equals(key)) 
			{
			result = hashTable[index].getValue();
			hashTable[index].value = value;
			return result;
			} else if(!collisionIncrement)
			{
			numberOfCollisions++;
			collisionIncrement = true;
			}
			index = (index + 1) % tableSize;
		}
		
		hashTable[index] = new Entry<>(key, value);
		numberOfEntries++;
		
		if(getLoadFactor()>loadFactor)
		{
			enlargeHashTable();
		}

		return result;
	}

    /**
     * Removes a key-value pair from the dictionary.
     * If the key does not match anything, return null.
     * @param key The key to be found to remove.
     * @param value The value being removed.
     * @return The value being removed.
     */
    	@Override
	public V remove(K key) 
	{
	throw new UnsupportedOperationException();
	}

    /**
     * Gets the value from a key.
     * If the key is not found, returns null.
     * @param key The key to be found.
     * @param value The associated value to be found.
     * @return The value being found.
     */
    	@Override
	public V getValue(K key) 
	{
		int index = getHashIndex(key);
		while (hashTable[index] != null) 
		{
			if (hashTable[index] != AVAILABLE && hashTable[index].key.equals(key)) 
			{
			return hashTable[index].value;
			}
			index = (index + 1) % tableSize;
		}
		return null;
	}

	public int getCollisionCount()
	{
		return numberOfCollisions;
	}

	private double getLoadFactor()
	{
		return (double) numberOfEntries / tableSize;
	}

	private void enlargeHashTable() 
	{
		Entry<K, V>[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(oldSize + oldSize); // Ensuring prime size
		checkSize(newSize);
	
		// Safe type cast for new Entry array
		@SuppressWarnings("unchecked")
		Entry<K, V>[] temp = (Entry<K, V>[]) new Entry[newSize];
		
		hashTable = temp; // Assign new table
		tableSize = newSize; // Update table size
		numberOfEntries = 0; // Reset count (add() will increment it)
	
		// Rehash dictionary entries from old table to new table
		for (int index = 0; index < oldSize; index++) 
		{
			if (oldTable[index] != null && oldTable[index] != AVAILABLE) 
			{
			add(oldTable[index].getKey(), oldTable[index].getValue()); // Use add() for proper hashing
			}
		}
	}

	/** Checks if the dictionary contains a key. */
	@Override
	public boolean contains(K key) 
	{
		return getValue(key) != null;
	}

	/** Gets the size of the dictionary. */
	@Override
	public int getSize() 
	{
		return numberOfEntries;
	}

	/** Checks if the dictionary is empty. */
	@Override
	public boolean isEmpty() 
	{
		return numberOfEntries == 0;
	}

	/** Clears the dictionary. */
	@Override
	public void clear() 
	{
		for(int index = 0; index < hashTable.length; index++) {
			hashTable[index] = null;
		}
	}

	/** Gets the hash index to use in the table*/
	private int getHashIndex(K key) 
	{
		int index = key.hashCode() % hashTable.length;
		
	if(index < 0)
	{
		index = index + hashTable.length;
	}

		return index;
	}

	/** Ensures the initial capacity is within allowed range. */
	private int checkCapacity(int capacity) 
	{
		return Math.min(capacity, MAX_CAPACITY);
	}

	/** Finds next prime number >= given number. */
	private int getNextPrime(int num) 
	{
		while (!isPrime(num)) num++;
		return num;
	}

	/** Checks if a number is prime. */
	private boolean isPrime(int num) 
	{
		if (num < 2) return false;
		for (int i = 2; i * i <= num; i++) {
			if (num % i == 0) return false;
		}
		return true;
	}

	/** Ensures size is not exceeding max. */
	private void checkSize(int size) 
	{
		if (size > MAX_CAPACITY) throw new IllegalStateException("Size exceeds max capacity.");
	}


	/** Inner class for key-value entries. */
	private static class Entry<K, V> 
	{
		private K key;
		private V value;

		private Entry(K keyV, V valueV) 
		{
			key = keyV;
			value = valueV;
		}

		private V getValue()
		{
			return value;
		}

		private K getKey()
		{
			return key;
		}

	}

	/** Iterator for keys. */
	private class KeyIterator implements Iterator<K> 
	{
		private int currentIndex = 0;

		public boolean hasNext() 
		{
			while (currentIndex < tableSize && (hashTable[currentIndex] == null || hashTable[currentIndex] == AVAILABLE)) 
			{
			currentIndex++;
			}
			return currentIndex < tableSize;
		}

		public K next() 
		{
			if (!hasNext()) throw new NoSuchElementException();
			return hashTable[currentIndex++].key;
		}
	}

	/** Iterator for values. */
	private class ValueIterator implements Iterator<V> 
	{
		private int currentIndex = 0;

		public boolean hasNext() 
		{
			throw new UnsupportedOperationException();
		}

		public V next() 
		{
			throw new UnsupportedOperationException();
		}
	}

	/** Returns an iterator for keys. */
	@Override
	public Iterator<K> getKeyIterator() 
	{
		return new KeyIterator();
	}

	/** Returns an iterator for values. */
	@Override
	public Iterator<V> getValueIterator() {
		throw new UnsupportedOperationException();
	}
}
