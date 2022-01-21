/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.knn.index;

import java.util.HashSet;
import java.util.Set;

/**
 * Enum contains spaces supported for approximate nearest neighbor search in the k-NN plugin. Each engine's methods are
 * expected to support a subset of these spaces. Validation should be done in the jni layer and an exception should be
 * propagated up to the Java layer. Additionally, naming translations should be done in jni layer as well. For example,
 * nmslib calls the inner_product space "negdotprod". This translation should take place in the nmslib's jni layer.
 */
public enum SpaceType {
  L2("l2") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1 / (1 + rawScore);
    }
  },
  COSINESIMIL("cosinesimil") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1 / (1 + rawScore);
    }
  },
  L1("l1") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1 / (1 + rawScore);
    }
  },
  LINF("linf") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1 / (1 + rawScore);
    }
  },
  INNER_PRODUCT("innerproduct") {
    /**
     * The inner product has a range of [-Float.MAX_VALUE, Float.MAX_VALUE], with a more similar result being
     * represented by a more negative value. In Lucene, scores have to be in the range of [0, Float.MAX_VALUE],
     * where a higher score represents a more similar result. So, we convert here.
     *
     * @param rawScore score returned from underlying library
     * @return Lucene scaled score
     */
    @Override
    public float scoreTranslation(float rawScore) {
      if (rawScore >= 0) {
        return 1 / (1 + rawScore);
      }
      return -rawScore + 1;
    }
  },
  HAMMING_BIT("hammingbit") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1 / (1 + rawScore);
    }
  },
  JACCARD_SPARSE("jaccard_sparse") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1.0f - rawScore;
    }
  },
  BIT_JACCARD("bit_jaccard") {
    @Override
    public float scoreTranslation(float rawScore) {
      return 1.0f - rawScore;
    }
  };

  public static SpaceType DEFAULT = L2;

  private final String value;

  SpaceType(String value) {
    this.value = value;
  }

  public abstract float scoreTranslation(float rawScore);

  /**
   * Get space type name in engine
   *
   * @return name
   */
  public String getValue() { return value; }

  public static Set<String> getValues() {
    Set<String> values = new HashSet<>();

    for (SpaceType spaceType : SpaceType.values()) {
      values.add(spaceType.getValue());
    }
    return values;
  }

  public static SpaceType getSpace(String spaceTypeName) {
    for (SpaceType currentSpaceType : SpaceType.values()) {
      if (currentSpaceType.getValue().equalsIgnoreCase(spaceTypeName)) {
        return currentSpaceType;
      }
    }
    throw new IllegalArgumentException("Unable to find space: " + spaceTypeName);
  }
}
