package fr.pigeo.rimap.rimaprcp.cachemanager.wwutil;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWXML;



public class FileStoreDataSet extends AVListImpl {
	
	 	public static final String HOUR = "gov.nasa.worldwindx.examples.util.cachecleaner.HOUR";
	    public static final String DAY = "gov.nasa.worldwindx.examples.util.cachecleaner.DAY";
	    public static final String WEEK = "gov.nasa.worldwindx.examples.util.cachecleaner.WEEK";
	    public static final String MONTH = "gov.nasa.worldwindx.examples.util.cachecleaner.MONTH";
	    public static final String YEAR = "gov.nasa.worldwindx.examples.util.cachecleaner.YEAR";
	    
	    //protected final String dataSetPath; // full path to data set in installed directory
	    //protected final String filestorePath; // full path to filestore root
	    //protected final String configFilePath; // full path to data set's config file

	    protected static class LeafInfo
	    {
	        long lastUsed;
	        long size;
	    }

	    protected final File root;
	    protected final String cacheRootPath;
	    protected ArrayList<LeafInfo> leafDirs = new ArrayList<LeafInfo>();
	    protected LeafInfo[] sortedLeafDirs;

	    
	     
	    
	    public FileStoreDataSet(File root, String cacheRootPath)
	    {
	        if (root == null)
	        {
	            String message = Logging.getMessage("nullValue.FileStorePathIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	        this.root = root;
	        this.cacheRootPath = cacheRootPath;
	        this.update();
	        
	    }

	    protected void update()
	    {
	        this.leafDirs.clear();
	        findLeaves(this.root, this.leafDirs);
	        if (this.leafDirs.size() == 0)
	            return;

	        this.sortedLeafDirs = new LeafInfo[this.leafDirs.size()];
	        this.sortedLeafDirs = this.leafDirs.toArray(this.sortedLeafDirs);
	        Arrays.sort(this.sortedLeafDirs, new Comparator<LeafInfo>()
	        {
	            public int compare(LeafInfo leafA, LeafInfo leafB)
	            {
	                return leafA.lastUsed < leafB.lastUsed ? -1 : leafA.lastUsed == leafB.lastUsed ? 0 : 1;
	            }
	        });
	    }
	    
	    
	    
	    public static List<FileStoreDataSet> getDataSets(File cacheRoot)
	    {
	        if (cacheRoot == null)
	        {
	            String message = Logging.getMessage("nullValue.FileStorePathIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	        ArrayList<FileStoreDataSet> datasets = new ArrayList<FileStoreDataSet>();
	        
	        

	        File[] cacheDirs = FileStoreDataSet.listDirs(cacheRoot);
	        
	        
	        for (File cacheDir : cacheDirs)
	        {
	            if (cacheDir.getName().equals("license"))
	                continue;

	            File[] subDirs = FileStoreDataSet.listDirs(cacheDir);
	            if (subDirs.length == 0)
	            {
	                datasets.add(new FileStoreDataSet(cacheDir, cacheRoot.getPath()));
	            }
	            else
	            {
	                // If the directory should be treated as a single dataset, add just one entry to the list.
	                if (isSingleDataSet(subDirs))
	                {
	                    datasets.add(new FileStoreDataSet(cacheDir, cacheRoot.getPath()));
	                }
	                // Otherwise add each subdirectory as a separate data set.
	                else
	                {
	                    for (File sd : subDirs)
	                    {
	                        FileStoreDataSet ds = new FileStoreDataSet(sd, cacheRoot.getPath());
	                        datasets.add(ds);
	                        
	                    }
	                }
	            }
	        }

	        return datasets;
	    }
	    
	    /**
	     * List all of the sub-directories in a parent directory.
	     *
	     * @param parent Parent directory to search.
	     *
	     * @return All sub-directories under {@code parent}.
	     */
	    protected static File[] listDirs(File parent)
	    {
	        return parent.listFiles(new FileFilter()
	        {
	            public boolean accept(File file)
	            {
	                return file.isDirectory();
	            }
	        });
	    }

	    /**
	     * Determines if a list of sub-directories should be treated as a single data set. This implementation returns
	     * {@code true} if all of the sub-directories have numeric names. In this case, the numeric directories are most
	     * likely used by the cache implementation to group files in a single data set. The numeric directory names do not
	     * provide meaningful grouping to the user.
	     *
	     * @param subDirs List of sub-directories to test.
	     *
	     * @return {@code true} if the directories should be treated as a single data set.
	     */
	    protected static boolean isSingleDataSet(File[] subDirs)
	    {
	        boolean onlyNumericDirs = true;

	        for (File sd : subDirs)
	        {
	            if (!isNumeric(sd.getName()))
	                onlyNumericDirs = false;
	        }

	        return onlyNumericDirs;
	    }
	    
	    /**
	     * Determines if a string contains only digits.
	     *
	     * @param s String to test.
	     *
	     * @return {@code true} if {@code s} contains only digits.
	     */
	    protected static boolean isNumeric(String s)
	    {
	        for (char c : s.toCharArray())
	        {
	            if (!Character.isDigit(c))
	                return false;
	        }
	        return true;
	    }
	    
	    
	    public String getPath()
	    {
	        return root.getPath();
	    }

	    public String getName()
	    {
	        String name = this.cacheRootPath == null ? this.getPath() : this.getPath().replace(
	            this.cacheRootPath.subSequence(0, this.cacheRootPath.length()), "".subSequence(0, 0));
	        return name.startsWith("/") ? name.substring(1) : name;
	    }

	    public long getSize()
	    {
	        long size = 0;

	        for (LeafInfo leaf : this.leafDirs)
	        {
	            size += leaf.size;
	        }

	        return size;
	    }
	    
	    public long getLastModified()
	    {
	        return this.sortedLeafDirs[this.sortedLeafDirs.length - 1].lastUsed;
	    }
	    
	    
	    
	    
	    protected static void findLeaves(File dir, ArrayList<LeafInfo> leaves)
	    {
	        if (!dir.isDirectory())
	            return;

	        File[] subDirs = dir.listFiles(new FileFilter()
	        {
	            public boolean accept(File file)
	            {
	                return file.isDirectory();
	            }
	        });

	        if (subDirs.length == 0)
	        {
	            LeafInfo li = new LeafInfo();
	            li.lastUsed = dir.lastModified();
	            li.size = computeDirectorySize(dir);
	            leaves.add(li);
	        }
	        else
	        {
	            for (File subDir : subDirs)
	            {
	                findLeaves(subDir, leaves);
	            }
	        }
	    }
	    
	    protected static long computeDirectorySize(File dir)
	    {
	        long size = 0;

	        File[] files = dir.listFiles();
	        for (File file : files)
	        {
	            try
	            {
	                FileInputStream fis = new FileInputStream(file);
	                size += fis.available();
	                fis.close();
	            }
	            catch (IOException e)
	            {
	                String message = Logging.getMessage("generic.ExceptionWhileComputingSize", file.getAbsolutePath());
	                Logging.logger().fine(message);
	            }
	        }

	        return size;
	    }
	    
	    
} 
	    
	    
	
